package br.com.biblioteca.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

// Resposta da BrasilAPI (/api/isbn/v1/{isbn}). Guardamos so os campos que usamos;
// os demais (synopsis, page_count, ...) sao ignorados.
@JsonIgnoreProperties(ignoreUnknown = true)
public record IsbnResponse(String title, List<String> authors, String publisher, Integer year) {
}
