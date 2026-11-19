package br.com.biblioteca.dto;

import jakarta.validation.constraints.NotNull;

// O usuario vem da sessao (quem esta logado); so precisamos do livro
public record ReservaRequest(@NotNull(message = "é obrigatório") Long livroId) {
}
