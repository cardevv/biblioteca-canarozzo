package br.com.biblioteca.controller;

import br.com.biblioteca.dto.LivroRequest;
import br.com.biblioteca.dto.LivroResponse;
import br.com.biblioteca.service.LivroService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Livros", description = "Acervo de livros")
@RestController
@RequestMapping("/api/livros")
public class LivroController {

    private final LivroService livroService;

    public LivroController(LivroService livroService) {
        this.livroService = livroService;
    }

    // GET /api/livros ou GET /api/livros?titulo=java
    @Operation(summary = "Lista livros, com busca opcional por título")
    @GetMapping
    public ResponseEntity<List<LivroResponse>> listar(@RequestParam(required = false) String titulo) {
        return ResponseEntity.ok(livroService.listar(titulo));
    }

    @Operation(summary = "Busca um livro pelo id")
    @GetMapping("/{id}")
    public ResponseEntity<LivroResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(livroService.buscar(id));
    }

    @Operation(summary = "Cadastra um livro (ADMIN)")
    @PostMapping
    public ResponseEntity<LivroResponse> criar(@Valid @RequestBody LivroRequest dados) {
        return ResponseEntity.status(HttpStatus.CREATED).body(livroService.criar(dados));
    }

    @Operation(summary = "Atualiza um livro (ADMIN)")
    @PutMapping("/{id}")
    public ResponseEntity<LivroResponse> atualizar(@PathVariable Long id, @Valid @RequestBody LivroRequest dados) {
        return ResponseEntity.ok(livroService.atualizar(id, dados));
    }

    @Operation(summary = "Exclui um livro sem exemplares (ADMIN)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        livroService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
