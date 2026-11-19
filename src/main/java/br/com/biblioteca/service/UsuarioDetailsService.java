package br.com.biblioteca.service;

import br.com.biblioteca.model.Usuario;
import br.com.biblioteca.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// O Spring Security chama esta classe no login para buscar o usuario no banco.
// O "username" do login e o email.
@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        // roles("ADMIN") vira a autoridade "ROLE_ADMIN", usada no hasRole("ADMIN")
        return User.withUsername(usuario.getEmail())
                .password(usuario.getSenha())
                .roles(usuario.getPapel().name())
                .build();
    }
}
