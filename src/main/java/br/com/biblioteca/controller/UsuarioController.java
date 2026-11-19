package br.com.biblioteca.controller;

import br.com.biblioteca.dto.UsuarioRequest;
import br.com.biblioteca.dto.UsuarioResponse;
import br.com.biblioteca.service.UsuarioService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Rotas /api/usuarios: so ADMIN (regra no SecurityConfig), exceto /me
@Tag(name = "Usuários", description = "Usuários do sistema")
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Dados do usuario logado. O Spring injeta o Authentication da sessao;
    // authentication.getName() e o email usado no login.
    @Operation(summary = "Dados do usuário logado")
    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> me(Authentication authentication) {
        return ResponseEntity.ok(UsuarioResponse.de(usuarioService.buscarPorEmail(authentication.getName())));
    }

    @Operation(summary = "Lista os usuários (ADMIN)")
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listar() {
        return ResponseEntity.ok(usuarioService.listar());
    }

    @Operation(summary = "Busca um usuário pelo id (ADMIN)")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscar(id));
    }

    @Operation(summary = "Cadastra um usuário (ADMIN)")
    @PostMapping
    public ResponseEntity<UsuarioResponse> criar(@Valid @RequestBody UsuarioRequest dados) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.criar(dados));
    }

    @Operation(summary = "Atualiza um usuário (ADMIN)")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> atualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequest dados) {
        return ResponseEntity.ok(usuarioService.atualizar(id, dados));
    }

    @Operation(summary = "Exclui um usuário (ADMIN)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        usuarioService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
