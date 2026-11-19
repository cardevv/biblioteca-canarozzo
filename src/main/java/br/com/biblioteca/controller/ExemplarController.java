package br.com.biblioteca.controller;

import br.com.biblioteca.dto.ExemplarRequest;
import br.com.biblioteca.dto.ExemplarResponse;
import br.com.biblioteca.service.ExemplarService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// /api/exemplares e so para ADMIN; /api/livros/{id}/exemplares qualquer usuario logado
// (regras no SecurityConfig)
@Tag(name = "Exemplares", description = "Cópias físicas dos livros")
@RestController
@RequestMapping("/api")
public class ExemplarController {

    private final ExemplarService exemplarService;

    public ExemplarController(ExemplarService exemplarService) {
        this.exemplarService = exemplarService;
    }

    @Operation(summary = "Lista todos os exemplares (ADMIN)")
    @GetMapping("/exemplares")
    public ResponseEntity<List<ExemplarResponse>> listar() {
        return ResponseEntity.ok(exemplarService.listar());
    }

    @Operation(summary = "Busca um exemplar pelo id (ADMIN)")
    @GetMapping("/exemplares/{id}")
    public ResponseEntity<ExemplarResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(exemplarService.buscar(id));
    }

    // Exemplares de um livro especifico
    @Operation(summary = "Lista os exemplares de um livro")
    @GetMapping("/livros/{livroId}/exemplares")
    public ResponseEntity<List<ExemplarResponse>> listarPorLivro(@PathVariable Long livroId) {
        return ResponseEntity.ok(exemplarService.listarPorLivro(livroId));
    }

    @Operation(summary = "Cria um exemplar com código automático (ADMIN)")
    @PostMapping("/exemplares")
    public ResponseEntity<ExemplarResponse> criar(@Valid @RequestBody ExemplarRequest dados) {
        return ResponseEntity.status(HttpStatus.CREATED).body(exemplarService.criar(dados));
    }

    @Operation(summary = "Exclui um exemplar que não está emprestado (ADMIN)")
    @DeleteMapping("/exemplares/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        exemplarService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
