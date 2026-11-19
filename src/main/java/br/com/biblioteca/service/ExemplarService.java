package br.com.biblioteca.service;

import br.com.biblioteca.dto.ExemplarRequest;
import br.com.biblioteca.dto.ExemplarResponse;
import br.com.biblioteca.exception.RecursoNaoEncontradoException;
import br.com.biblioteca.exception.RegraNegocioException;
import br.com.biblioteca.model.Exemplar;
import br.com.biblioteca.model.Livro;
import br.com.biblioteca.model.StatusExemplar;
import br.com.biblioteca.repository.ExemplarRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExemplarService {

    private final ExemplarRepository exemplarRepository;
    private final LivroService livroService;

    public ExemplarService(ExemplarRepository exemplarRepository, LivroService livroService) {
        this.exemplarRepository = exemplarRepository;
        this.livroService = livroService;
    }

    @Transactional(readOnly = true)
    public List<ExemplarResponse> listar() {
        return exemplarRepository.findAllByOrderByCodigo().stream().map(ExemplarResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public List<ExemplarResponse> listarPorLivro(Long livroId) {
        livroService.buscarEntidade(livroId); // 404 se o livro nao existir
        return exemplarRepository.findByLivroIdOrderByCodigo(livroId).stream().map(ExemplarResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public ExemplarResponse buscar(Long id) {
        return ExemplarResponse.de(buscarEntidade(id));
    }

    public Exemplar buscarEntidade(Long id) {
        return exemplarRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Exemplar não encontrado: " + id));
    }

    @Transactional
    public ExemplarResponse criar(ExemplarRequest dados) {
        Livro livro = livroService.buscarEntidade(dados.livroId());
        Exemplar exemplar = new Exemplar(gerarCodigo(livro.getId()), livro);
        return ExemplarResponse.de(exemplarRepository.save(exemplar));
    }

    @Transactional
    public void excluir(Long id) {
        Exemplar exemplar = buscarEntidade(id);
        // Regra: exemplar emprestado nao pode ser excluido
        if (exemplar.getStatus() == StatusExemplar.EMPRESTADO) {
            throw new RegraNegocioException("Não é possível excluir um exemplar emprestado");
        }
        exemplarRepository.delete(exemplar);
    }

    // Gera o codigo "LIV{idDoLivro}-EX{numero}". Se o numero ja existir
    // (ex.: um exemplar foi excluido), tenta o proximo.
    private String gerarCodigo(Long livroId) {
        long numero = exemplarRepository.countByLivroId(livroId) + 1;
        String codigo = "LIV" + livroId + "-EX" + numero;
        while (exemplarRepository.existsByCodigo(codigo)) {
            numero++;
            codigo = "LIV" + livroId + "-EX" + numero;
        }
        return codigo;
    }
}
