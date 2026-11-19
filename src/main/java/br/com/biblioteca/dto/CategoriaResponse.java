package br.com.biblioteca.dto;

import br.com.biblioteca.model.Categoria;

// Dados devolvidos pela API (nao expomos a entidade diretamente)
public record CategoriaResponse(Long id, String nome) {

    public static CategoriaResponse de(Categoria categoria) {
        return new CategoriaResponse(categoria.getId(), categoria.getNome());
    }
}
