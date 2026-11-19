package br.com.biblioteca.dto;

import jakarta.validation.constraints.NotNull;

// So precisa do livro: o codigo e gerado e o status comeca DISPONIVEL
public record ExemplarRequest(@NotNull(message = "é obrigatório") Long livroId) {
}
