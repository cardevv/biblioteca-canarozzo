package br.com.biblioteca.service;

import br.com.biblioteca.dto.UsuarioRequest;
import br.com.biblioteca.dto.UsuarioResponse;
import br.com.biblioteca.exception.RecursoNaoEncontradoException;
import br.com.biblioteca.exception.RegraNegocioException;
import br.com.biblioteca.model.Usuario;
import br.com.biblioteca.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream().map(UsuarioResponse::de).toList();
    }

    public UsuarioResponse buscar(Long id) {
        return UsuarioResponse.de(buscarEntidade(id));
    }

    public Usuario buscarEntidade(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado: " + id));
    }

    // Busca o usuario pelo email (o email e o "username" do login)
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado: " + email));
    }

    @Transactional
    public UsuarioResponse criar(UsuarioRequest dados) {
        if (dados.senha() == null || dados.senha().isBlank()) {
            throw new RegraNegocioException("A senha é obrigatória no cadastro");
        }
        if (usuarioRepository.existsByEmail(dados.email())) {
            throw new RegraNegocioException("Já existe usuário com o e-mail " + dados.email());
        }
        // A senha e criptografada com BCrypt antes de ir para o banco
        Usuario usuario = new Usuario(dados.nome(), dados.email(), passwordEncoder.encode(dados.senha()), dados.papel());
        return UsuarioResponse.de(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponse atualizar(Long id, UsuarioRequest dados) {
        Usuario usuario = buscarEntidade(id);

        // Se trocou o email, nao pode ser o email de outro usuario
        if (!usuario.getEmail().equals(dados.email()) && usuarioRepository.existsByEmail(dados.email())) {
            throw new RegraNegocioException("Já existe usuário com o e-mail " + dados.email());
        }
        usuario.setNome(dados.nome());
        usuario.setEmail(dados.email());
        usuario.setPapel(dados.papel());
        // Senha vazia = mantem a senha atual
        if (dados.senha() != null && !dados.senha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(dados.senha()));
        }
        // Dentro do @Transactional, o Hibernate salva as alteracoes sozinho ao final
        return UsuarioResponse.de(usuario);
    }

    @Transactional
    public void excluir(Long id) {
        Usuario usuario = buscarEntidade(id);
        usuarioRepository.delete(usuario);
    }
}
