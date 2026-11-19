package br.com.biblioteca.controller;

import br.com.biblioteca.dto.CategoriaRequest;
import br.com.biblioteca.dto.CategoriaResponse;
import br.com.biblioteca.service.CategoriaService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// GET liberado para usuarios logados; POST/PUT/DELETE so ADMIN (SecurityConfig)
@Tag(name = "Categorias", description = "Cadastro de categorias")
@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @Operation(summary = "Lista todas as categorias")
    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> listar() {
        return ResponseEntity.ok(categoriaService.listar());
    }

    @Operation(summary = "Busca uma categoria pelo id")
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.buscar(id));
    }

    @Operation(summary = "Cadastra uma categoria (ADMIN)")
    @PostMapping
    public ResponseEntity<CategoriaResponse> criar(@Valid @RequestBody CategoriaRequest dados) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.criar(dados));
    }

    @Operation(summary = "Atualiza uma categoria (ADMIN)")
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponse> atualizar(@PathVariable Long id, @Valid @RequestBody CategoriaRequest dados) {
        return ResponseEntity.ok(categoriaService.atualizar(id, dados));
    }

    @Operation(summary = "Exclui uma categoria sem livros (ADMIN)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        categoriaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
