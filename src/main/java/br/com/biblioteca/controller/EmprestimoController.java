package br.com.biblioteca.controller;

import br.com.biblioteca.dto.DevolucaoResponse;
import br.com.biblioteca.dto.EmprestimoRequest;
import br.com.biblioteca.dto.EmprestimoResponse;
import br.com.biblioteca.model.StatusEmprestimo;
import br.com.biblioteca.service.EmprestimoService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Tudo aqui e so ADMIN, exceto GET /meus (regras no SecurityConfig)
@Tag(name = "Empréstimos", description = "Empréstimos e devoluções")
@RestController
@RequestMapping("/api/emprestimos")
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    public EmprestimoController(EmprestimoService emprestimoService) {
        this.emprestimoService = emprestimoService;
    }

    // GET /api/emprestimos ou GET /api/emprestimos?status=ATIVO
    @Operation(summary = "Lista empréstimos, com filtro opcional por status (ADMIN)")
    @GetMapping
    public ResponseEntity<List<EmprestimoResponse>> listar(@RequestParam(required = false) StatusEmprestimo status) {
        return ResponseEntity.ok(emprestimoService.listar(status));
    }

    // Emprestimos do usuario logado
    @Operation(summary = "Lista os empréstimos do usuário logado")
    @GetMapping("/meus")
    public ResponseEntity<List<EmprestimoResponse>> meus(Authentication authentication) {
        return ResponseEntity.ok(emprestimoService.listarDoUsuario(authentication.getName()));
    }

    // POST /api/emprestimos {"usuarioId": 2, "exemplarId": 1}
    @Operation(summary = "Registra um empréstimo de 14 dias (ADMIN)")
    @PostMapping
    public ResponseEntity<EmprestimoResponse> emprestar(@Valid @RequestBody EmprestimoRequest dados) {
        return ResponseEntity.status(HttpStatus.CREATED).body(emprestimoService.emprestar(dados));
    }

    // PUT /api/emprestimos/5/devolucao -> devolve e calcula atraso/multa
    @Operation(summary = "Registra a devolução e calcula atraso e multa (ADMIN)")
    @PutMapping("/{id}/devolucao")
    public ResponseEntity<DevolucaoResponse> devolver(@PathVariable Long id) {
        return ResponseEntity.ok(emprestimoService.devolver(id));
    }
}
