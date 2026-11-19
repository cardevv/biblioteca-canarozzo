package br.com.biblioteca.dto;

import br.com.biblioteca.model.Reserva;
import br.com.biblioteca.model.StatusReserva;

import java.time.LocalDate;

public record ReservaResponse(
        Long id,
        Long usuarioId,
        String usuarioNome,
        Long livroId,
        String livroTitulo,
        LocalDate dataReserva,
        StatusReserva status
) {

    public static ReservaResponse de(Reserva r) {
        return new ReservaResponse(
                r.getId(),
                r.getUsuario().getId(),
                r.getUsuario().getNome(),
                r.getLivro().getId(),
                r.getLivro().getTitulo(),
                r.getDataReserva(),
                r.getStatus()
        );
    }
}
