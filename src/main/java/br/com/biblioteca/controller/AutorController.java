package br.com.biblioteca.controller;

import br.com.biblioteca.dto.AutorRequest;
import br.com.biblioteca.dto.AutorResponse;
import br.com.biblioteca.service.AutorService;
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

@Tag(name = "Autores", description = "Cadastro de autores")
@RestController
@RequestMapping("/api/autores")
public class AutorController {

    private final AutorService autorService;

    public AutorController(AutorService autorService) {
        this.autorService = autorService;
    }

    @Operation(summary = "Lista todos os autores")
    @GetMapping
    public ResponseEntity<List<AutorResponse>> listar() {
        return ResponseEntity.ok(autorService.listar());
    }

    @Operation(summary = "Busca um autor pelo id")
    @GetMapping("/{id}")
    public ResponseEntity<AutorResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(autorService.buscar(id));
    }

    @Operation(summary = "Cadastra um autor (ADMIN)")
    @PostMapping
    public ResponseEntity<AutorResponse> criar(@Valid @RequestBody AutorRequest dados) {
        return ResponseEntity.status(HttpStatus.CREATED).body(autorService.criar(dados));
    }

    @Operation(summary = "Atualiza um autor (ADMIN)")
    @PutMapping("/{id}")
    public ResponseEntity<AutorResponse> atualizar(@PathVariable Long id, @Valid @RequestBody AutorRequest dados) {
        return ResponseEntity.ok(autorService.atualizar(id, dados));
    }

    @Operation(summary = "Exclui um autor sem livros (ADMIN)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        autorService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
