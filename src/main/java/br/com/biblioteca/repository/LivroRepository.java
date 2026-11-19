package br.com.biblioteca.repository;

import br.com.biblioteca.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LivroRepository extends JpaRepository<Livro, Long> {

    // Busca por parte do titulo: WHERE UPPER(titulo) LIKE UPPER('%texto%')
    List<Livro> findByTituloContainingIgnoreCaseOrderByTitulo(String titulo);

    List<Livro> findAllByOrderByTitulo();

    Optional<Livro> findByIsbn(String isbn);

    // Usados para impedir excluir categoria/autor que ainda tem livros
    boolean existsByCategoriaId(Long categoriaId);

    boolean existsByAutoresId(Long autorId);
}
