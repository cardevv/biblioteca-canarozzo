package br.com.biblioteca.repository;

import br.com.biblioteca.model.Exemplar;
import br.com.biblioteca.model.StatusExemplar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExemplarRepository extends JpaRepository<Exemplar, Long> {

    // Exemplares de um livro com determinado status (ex.: DISPONIVEL)
    List<Exemplar> findByLivroIdAndStatus(Long livroId, StatusExemplar status);

    // Quantos exemplares de um livro estao com determinado status
    long countByLivroIdAndStatus(Long livroId, StatusExemplar status);
}
