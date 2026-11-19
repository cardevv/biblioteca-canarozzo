package br.com.biblioteca.dto;

import br.com.biblioteca.model.Papel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// DTO de entrada para cadastrar/editar usuario.
// Na edicao, a senha pode vir vazia para manter a atual.
public record UsuarioRequest(
        @NotBlank(message = "é obrigatório") String nome,
        @NotBlank(message = "é obrigatório") @Email(message = "inválido") String email,
        String senha,
        @NotNull(message = "é obrigatório") Papel papel
) {
}
