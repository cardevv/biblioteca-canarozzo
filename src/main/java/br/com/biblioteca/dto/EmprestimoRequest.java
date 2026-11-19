package br.com.biblioteca.dto;

import jakarta.validation.constraints.NotNull;

public record EmprestimoRequest(
        @NotNull(message = "é obrigatório") Long usuarioId,
        @NotNull(message = "é obrigatório") Long exemplarId
) {
}
