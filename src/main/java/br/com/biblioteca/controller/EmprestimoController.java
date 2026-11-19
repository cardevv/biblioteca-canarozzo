package br.com.biblioteca.controller;

import br.com.biblioteca.dto.EmprestimoRequest;
import br.com.biblioteca.dto.EmprestimoResponse;
import br.com.biblioteca.service.EmprestimoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/emprestimos")
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    public EmprestimoController(EmprestimoService emprestimoService) {
        this.emprestimoService = emprestimoService;
    }

    // POST /api/emprestimos {"usuarioId": 2, "exemplarId": 1} (so ADMIN)
    @PostMapping
    public ResponseEntity<EmprestimoResponse> emprestar(@Valid @RequestBody EmprestimoRequest dados) {
        return ResponseEntity.status(HttpStatus.CREATED).body(emprestimoService.emprestar(dados));
    }
}
