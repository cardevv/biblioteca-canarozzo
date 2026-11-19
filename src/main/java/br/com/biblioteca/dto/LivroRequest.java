package br.com.biblioteca.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

// O front manda so os IDs da categoria e dos autores; o Service busca as entidades
public record LivroRequest(
        @NotBlank(message = "é obrigatório") String titulo,
        @NotBlank(message = "é obrigatório") String isbn,
        String editora,
        Integer anoPublicacao,
        @NotNull(message = "é obrigatório") Long categoriaId,
        @NotEmpty(message = "informe pelo menos um autor") List<Long> autorIds
) {
}
