package br.com.biblioteca.dto;

import br.com.biblioteca.model.Autor;
import br.com.biblioteca.model.Livro;

import java.util.Comparator;
import java.util.List;

// Devolve o nome da categoria e os nomes dos autores (e os IDs, usados no formulario de edicao)
public record LivroResponse(
        Long id,
        String titulo,
        String isbn,
        String editora,
        Integer anoPublicacao,
        Long categoriaId,
        String categoria,
        List<Long> autorIds,
        List<String> autores
) {

    public static LivroResponse de(Livro livro) {
        List<Autor> autoresOrdenados = livro.getAutores().stream()
                .sorted(Comparator.comparing(Autor::getNome))
                .toList();
        return new LivroResponse(
                livro.getId(),
                livro.getTitulo(),
                livro.getIsbn(),
                livro.getEditora(),
                livro.getAnoPublicacao(),
                livro.getCategoria().getId(),
                livro.getCategoria().getNome(),
                autoresOrdenados.stream().map(Autor::getId).toList(),
                autoresOrdenados.stream().map(Autor::getNome).toList()
        );
    }
}
