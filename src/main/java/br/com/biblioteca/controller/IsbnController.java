package br.com.biblioteca.controller;

import br.com.biblioteca.dto.IsbnResponse;
import br.com.biblioteca.service.IsbnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Consulta de livro na API externa (so ADMIN, usado no cadastro de livros)
@Tag(name = "ISBN (BrasilAPI)", description = "Consulta de livros na API externa")
@RestController
@RequestMapping("/api/isbn")
public class IsbnController {

    private final IsbnService isbnService;

    public IsbnController(IsbnService isbnService) {
        this.isbnService = isbnService;
    }

    @Operation(summary = "Consulta título, autores, editora e ano pelo ISBN (ADMIN)")
    @GetMapping("/{isbn}")
    public ResponseEntity<IsbnResponse> consultar(@PathVariable String isbn) {
        return ResponseEntity.ok(isbnService.consultar(isbn));
    }
}
