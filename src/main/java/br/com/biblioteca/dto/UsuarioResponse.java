package br.com.biblioteca.dto;

import br.com.biblioteca.model.Papel;
import br.com.biblioteca.model.Usuario;

// DTO de saida: devolve os dados do usuario SEM a senha
public record UsuarioResponse(Long id, String nome, String email, Papel papel) {

    public static UsuarioResponse de(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getPapel());
    }
}
