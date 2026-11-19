package br.com.biblioteca.dto;

import jakarta.validation.constraints.NotBlank;

// Dados recebidos para cadastrar/editar uma categoria
public record CategoriaRequest(@NotBlank(message = "é obrigatório") String nome) {
}
