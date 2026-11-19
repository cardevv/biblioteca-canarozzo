package br.com.biblioteca.repository;

import br.com.biblioteca.model.Reserva;
import br.com.biblioteca.model.StatusReserva;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    // Regra: o usuario nao pode ter duas reservas ATIVAS do mesmo livro
    boolean existsByUsuarioIdAndLivroIdAndStatus(Long usuarioId, Long livroId, StatusReserva status);
}
