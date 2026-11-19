package br.com.biblioteca.repository;

import br.com.biblioteca.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LivroRepository extends JpaRepository<Livro, Long> {

    // Usados para impedir excluir categoria/autor que ainda tem livros
    boolean existsByCategoriaId(Long categoriaId);

    boolean existsByAutoresId(Long autorId);
}
