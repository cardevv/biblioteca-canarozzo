package br.com.biblioteca.controller;

import br.com.biblioteca.dto.ReservaRequest;
import br.com.biblioteca.dto.ReservaResponse;
import br.com.biblioteca.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// authentication.getName() = email do usuario logado (vem da sessao)
@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    // Todas as reservas (so ADMIN - SecurityConfig)
    @GetMapping
    public ResponseEntity<List<ReservaResponse>> listar() {
        return ResponseEntity.ok(reservaService.listar());
    }

    @GetMapping("/minhas")
    public ResponseEntity<List<ReservaResponse>> minhas(Authentication authentication) {
        return ResponseEntity.ok(reservaService.listarDoUsuario(authentication.getName()));
    }

    // POST /api/reservas {"livroId": 3}
    @PostMapping
    public ResponseEntity<ReservaResponse> reservar(@Valid @RequestBody ReservaRequest dados,
                                                    Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservaService.reservar(dados, authentication.getName()));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<ReservaResponse> cancelar(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(reservaService.cancelar(id, authentication.getName()));
    }

    @PutMapping("/{id}/atender")
    public ResponseEntity<ReservaResponse> atender(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(reservaService.atender(id, authentication.getName()));
    }
}
