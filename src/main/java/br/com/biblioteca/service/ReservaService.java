package br.com.biblioteca.service;

import br.com.biblioteca.dto.ReservaRequest;
import br.com.biblioteca.dto.ReservaResponse;
import br.com.biblioteca.exception.RecursoNaoEncontradoException;
import br.com.biblioteca.exception.RegraNegocioException;
import br.com.biblioteca.model.Livro;
import br.com.biblioteca.model.Papel;
import br.com.biblioteca.model.Reserva;
import br.com.biblioteca.model.StatusExemplar;
import br.com.biblioteca.model.StatusReserva;
import br.com.biblioteca.model.Usuario;
import br.com.biblioteca.repository.ExemplarRepository;
import br.com.biblioteca.repository.LivroRepository;
import br.com.biblioteca.repository.ReservaRepository;
import br.com.biblioteca.repository.UsuarioRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final LivroRepository livroRepository;
    private final ExemplarRepository exemplarRepository;
    private final UsuarioRepository usuarioRepository;

    public ReservaService(ReservaRepository reservaRepository, LivroRepository livroRepository,
                          ExemplarRepository exemplarRepository, UsuarioRepository usuarioRepository) {
        this.reservaRepository = reservaRepository;
        this.livroRepository = livroRepository;
        this.exemplarRepository = exemplarRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Todas as reservas (ADMIN)
    @Transactional(readOnly = true)
    public List<ReservaResponse> listar() {
        return reservaRepository.findAllByOrderByIdDesc().stream().map(ReservaResponse::de).toList();
    }

    // Reservas do usuario logado
    @Transactional(readOnly = true)
    public List<ReservaResponse> listarDoUsuario(String email) {
        return reservaRepository.findByUsuarioEmailOrderByIdDesc(email).stream().map(ReservaResponse::de).toList();
    }

    @Transactional
    public ReservaResponse reservar(ReservaRequest dados, String emailUsuario) {
        Usuario usuario = buscarUsuario(emailUsuario);
        Livro livro = livroRepository.findById(dados.livroId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Livro não encontrado: " + dados.livroId()));

        // Regra 1: so pode reservar se NAO houver exemplar disponivel
        // (se houver, o leitor deve pegar emprestado direto no balcao)
        long disponiveis = exemplarRepository.countByLivroIdAndStatus(livro.getId(), StatusExemplar.DISPONIVEL);
        if (disponiveis > 0) {
            throw new RegraNegocioException("O livro possui exemplar disponível; não é necessário reservar");
        }

        // Regra 2: nao pode haver outra reserva ATIVA do mesmo usuario para o mesmo livro
        if (reservaRepository.existsByUsuarioIdAndLivroIdAndStatus(usuario.getId(), livro.getId(), StatusReserva.ATIVA)) {
            throw new RegraNegocioException("Você já possui uma reserva ativa deste livro");
        }

        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setLivro(livro);
        reserva.setDataReserva(LocalDate.now());
        reserva.setStatus(StatusReserva.ATIVA);
        return ReservaResponse.de(reservaRepository.save(reserva));
    }

    // Cancelar: so o dono da reserva ou um ADMIN
    @Transactional
    public ReservaResponse cancelar(Long id, String emailUsuario) {
        Reserva reserva = buscarReserva(id);
        Usuario usuario = buscarUsuario(emailUsuario);

        boolean ehDono = reserva.getUsuario().getId().equals(usuario.getId());
        if (!ehDono && usuario.getPapel() != Papel.ADMIN) {
            // AccessDeniedException e convertida pelo Spring Security em HTTP 403
            throw new AccessDeniedException("Você só pode cancelar as suas reservas");
        }
        exigirAtiva(reserva);
        reserva.setStatus(StatusReserva.CANCELADA);
        return ReservaResponse.de(reserva);
    }

    // Atender: o ADMIN marca a reserva como atendida quando o livro e entregue ao leitor
    @Transactional
    public ReservaResponse atender(Long id, String emailUsuario) {
        Reserva reserva = buscarReserva(id);
        if (buscarUsuario(emailUsuario).getPapel() != Papel.ADMIN) {
            throw new AccessDeniedException("Somente ADMIN pode atender reservas");
        }
        exigirAtiva(reserva);
        reserva.setStatus(StatusReserva.ATENDIDA);
        return ReservaResponse.de(reserva);
    }

    private void exigirAtiva(Reserva reserva) {
        if (reserva.getStatus() != StatusReserva.ATIVA) {
            throw new RegraNegocioException("A reserva não está ativa (status atual: " + reserva.getStatus() + ")");
        }
    }

    private Reserva buscarReserva(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Reserva não encontrada: " + id));
    }

    private Usuario buscarUsuario(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado: " + email));
    }
}
