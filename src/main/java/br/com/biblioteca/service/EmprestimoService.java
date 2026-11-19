package br.com.biblioteca.service;

import br.com.biblioteca.dto.DevolucaoResponse;
import br.com.biblioteca.dto.EmprestimoRequest;
import br.com.biblioteca.dto.EmprestimoResponse;
import br.com.biblioteca.exception.RecursoNaoEncontradoException;
import br.com.biblioteca.exception.RegraNegocioException;
import br.com.biblioteca.model.Emprestimo;
import br.com.biblioteca.model.Exemplar;
import br.com.biblioteca.model.StatusEmprestimo;
import br.com.biblioteca.model.StatusExemplar;
import br.com.biblioteca.model.Usuario;
import br.com.biblioteca.repository.EmprestimoRepository;
import br.com.biblioteca.repository.ExemplarRepository;
import br.com.biblioteca.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class EmprestimoService {

    // Constantes das regras de negocio
    public static final int PRAZO_DIAS = 14;
    public static final int LIMITE_EMPRESTIMOS_ATIVOS = 3;
    public static final BigDecimal MULTA_POR_DIA = new BigDecimal("2.00");

    private final EmprestimoRepository emprestimoRepository;
    private final ExemplarRepository exemplarRepository;
    private final UsuarioRepository usuarioRepository;

    public EmprestimoService(EmprestimoRepository emprestimoRepository,
                             ExemplarRepository exemplarRepository,
                             UsuarioRepository usuarioRepository) {
        this.emprestimoRepository = emprestimoRepository;
        this.exemplarRepository = exemplarRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Lista todos ou filtra pelo status (ATIVO/DEVOLVIDO)
    @Transactional(readOnly = true)
    public List<EmprestimoResponse> listar(StatusEmprestimo status) {
        List<Emprestimo> emprestimos = (status == null)
                ? emprestimoRepository.findAllByOrderByIdDesc()
                : emprestimoRepository.findByStatusOrderByIdDesc(status);
        return emprestimos.stream().map(EmprestimoResponse::de).toList();
    }

    // Emprestimos do usuario logado (identificado pelo email do login)
    @Transactional(readOnly = true)
    public List<EmprestimoResponse> listarDoUsuario(String email) {
        return emprestimoRepository.findByUsuarioEmailOrderByIdDesc(email).stream()
                .map(EmprestimoResponse::de).toList();
    }

    @Transactional
    public EmprestimoResponse emprestar(EmprestimoRequest dados) {
        Usuario usuario = usuarioRepository.findById(dados.usuarioId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado: " + dados.usuarioId()));
        Exemplar exemplar = exemplarRepository.findById(dados.exemplarId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Exemplar não encontrado: " + dados.exemplarId()));

        // Regra 1: o exemplar precisa estar disponivel
        if (exemplar.getStatus() != StatusExemplar.DISPONIVEL) {
            throw new RegraNegocioException("O exemplar " + exemplar.getCodigo() + " não está disponível");
        }

        // Regra 2: no maximo 3 emprestimos ativos por usuario
        long ativos = emprestimoRepository.countByUsuarioIdAndStatus(usuario.getId(), StatusEmprestimo.ATIVO);
        if (ativos >= LIMITE_EMPRESTIMOS_ATIVOS) {
            throw new RegraNegocioException("O usuário já possui " + LIMITE_EMPRESTIMOS_ATIVOS + " empréstimos ativos");
        }

        // Regra 3: prazo de 14 dias
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setUsuario(usuario);
        emprestimo.setExemplar(exemplar);
        emprestimo.setDataEmprestimo(LocalDate.now());
        emprestimo.setDataPrevistaDevolucao(LocalDate.now().plusDays(PRAZO_DIAS));
        emprestimo.setStatus(StatusEmprestimo.ATIVO);

        // O exemplar passa a ficar EMPRESTADO
        exemplar.setStatus(StatusExemplar.EMPRESTADO);

        return EmprestimoResponse.de(emprestimoRepository.save(emprestimo));
    }

    @Transactional
    public DevolucaoResponse devolver(Long emprestimoId) {
        Emprestimo emprestimo = emprestimoRepository.findById(emprestimoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empréstimo não encontrado: " + emprestimoId));

        if (emprestimo.getStatus() == StatusEmprestimo.DEVOLVIDO) {
            throw new RegraNegocioException("Este empréstimo já foi devolvido");
        }

        LocalDate hoje = LocalDate.now();
        emprestimo.setDataDevolucao(hoje);
        emprestimo.setStatus(StatusEmprestimo.DEVOLVIDO);
        // O exemplar volta a ficar disponivel
        emprestimo.getExemplar().setStatus(StatusExemplar.DISPONIVEL);

        // Dias de atraso = dias entre a data prevista e hoje (nunca negativo)
        long diasAtraso = Math.max(0, ChronoUnit.DAYS.between(emprestimo.getDataPrevistaDevolucao(), hoje));
        // Multa: R$ 2,00 por dia de atraso (BigDecimal evita erro de arredondamento com dinheiro)
        BigDecimal valorMulta = MULTA_POR_DIA.multiply(BigDecimal.valueOf(diasAtraso));

        return new DevolucaoResponse(
                emprestimo.getId(),
                emprestimo.getExemplar().getLivro().getTitulo(),
                emprestimo.getExemplar().getCodigo(),
                emprestimo.getDataPrevistaDevolucao(),
                hoje,
                diasAtraso,
                valorMulta
        );
    }
}
