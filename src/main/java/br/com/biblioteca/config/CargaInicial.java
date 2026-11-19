package br.com.biblioteca.config;

import br.com.biblioteca.model.Papel;
import br.com.biblioteca.model.Usuario;
import br.com.biblioteca.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// CommandLineRunner: o Spring executa o metodo run() logo depois que a aplicacao sobe.
// Usamos para cadastrar dados de exemplo (so cria se ainda nao existir).
@Component
public class CargaInicial implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public CargaInicial(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        criarUsuario("Administrador", "admin@biblioteca.com", "admin123", Papel.ADMIN);
        criarUsuario("Leitor de Teste", "leitor@biblioteca.com", "leitor123", Papel.LEITOR);
    }

    private void criarUsuario(String nome, String email, String senha, Papel papel) {
        if (!usuarioRepository.existsByEmail(email)) {
            usuarioRepository.save(new Usuario(nome, email, passwordEncoder.encode(senha), papel));
        }
    }
}
