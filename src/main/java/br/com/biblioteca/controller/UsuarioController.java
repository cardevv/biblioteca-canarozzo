package br.com.biblioteca.controller;

import br.com.biblioteca.dto.UsuarioResponse;
import br.com.biblioteca.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Dados do usuario logado. O Spring injeta o Authentication da sessao;
    // authentication.getName() e o email usado no login.
    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> me(Authentication authentication) {
        return ResponseEntity.ok(UsuarioResponse.de(usuarioService.buscarPorEmail(authentication.getName())));
    }
}
