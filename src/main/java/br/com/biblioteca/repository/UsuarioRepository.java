package br.com.biblioteca.repository;

import br.com.biblioteca.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// O Spring Data gera a implementacao (save, findAll, findById, delete...) automaticamente
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Consulta derivada do nome do metodo: SELECT * FROM usuario WHERE email = ?
    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);
}
