package br.com.biblioteca.dto;

import jakarta.validation.constraints.NotBlank;

public record AutorRequest(
        @NotBlank(message = "é obrigatório") String nome,
        String nacionalidade
) {
}
