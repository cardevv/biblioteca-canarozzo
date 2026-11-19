package br.com.biblioteca.repository;

import br.com.biblioteca.model.Reserva;
import br.com.biblioteca.model.StatusReserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    // Regra: o usuario nao pode ter duas reservas ATIVAS do mesmo livro
    boolean existsByUsuarioIdAndLivroIdAndStatus(Long usuarioId, Long livroId, StatusReserva status);

    List<Reserva> findAllByOrderByIdDesc();

    List<Reserva> findByUsuarioEmailOrderByIdDesc(String email);
}
