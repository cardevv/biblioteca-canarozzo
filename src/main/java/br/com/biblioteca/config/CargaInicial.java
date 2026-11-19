package br.com.biblioteca.config;

import br.com.biblioteca.model.Autor;
import br.com.biblioteca.model.Categoria;
import br.com.biblioteca.model.Livro;
import br.com.biblioteca.model.Papel;
import br.com.biblioteca.model.Usuario;
import br.com.biblioteca.repository.AutorRepository;
import br.com.biblioteca.repository.CategoriaRepository;
import br.com.biblioteca.repository.LivroRepository;
import br.com.biblioteca.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

// CommandLineRunner: o Spring executa o metodo run() logo depois que a aplicacao sobe.
// Usamos para cadastrar dados de exemplo (so cria se ainda nao existir).
@Component
public class CargaInicial implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final AutorRepository autorRepository;
    private final LivroRepository livroRepository;
    private final PasswordEncoder passwordEncoder;

    public CargaInicial(UsuarioRepository usuarioRepository, CategoriaRepository categoriaRepository,
                        AutorRepository autorRepository, LivroRepository livroRepository,
                        PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
        this.autorRepository = autorRepository;
        this.livroRepository = livroRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        criarUsuario("Administrador", "admin@biblioteca.com", "admin123", Papel.ADMIN);
        criarUsuario("Leitor de Teste", "leitor@biblioteca.com", "leitor123", Papel.LEITOR);
        criarAcervo();
    }

    private void criarUsuario(String nome, String email, String senha, Papel papel) {
        if (!usuarioRepository.existsByEmail(email)) {
            usuarioRepository.save(new Usuario(nome, email, passwordEncoder.encode(senha), papel));
        }
    }

    // Categorias, autores e livros de exemplo (so na primeira execucao)
    private void criarAcervo() {
        if (livroRepository.count() > 0) {
            return;
        }

        Categoria romance = categoriaRepository.save(new Categoria("Romance"));
        Categoria ficcao = categoriaRepository.save(new Categoria("Ficção"));
        Categoria tecnologia = categoriaRepository.save(new Categoria("Tecnologia"));

        Autor machado = autorRepository.save(new Autor("Machado de Assis", "Brasileira"));
        Autor orwell = autorRepository.save(new Autor("George Orwell", "Britânica"));
        Autor martin = autorRepository.save(new Autor("Robert C. Martin", "Americana"));
        Autor gamma = autorRepository.save(new Autor("Erich Gamma", "Suíça"));
        Autor helm = autorRepository.save(new Autor("Richard Helm", "Australiana"));

        criarLivro("Dom Casmurro", "9788594318602", "Principis", 2019, romance, Set.of(machado));
        criarLivro("Memórias Póstumas de Brás Cubas", "9788525406958", "L&PM", 1997, romance, Set.of(machado));
        criarLivro("1984", "9788535914849", "Companhia das Letras", 2009, ficcao, Set.of(orwell));
        criarLivro("Código Limpo", "9788576082675", "Alta Books", 2009, tecnologia, Set.of(martin));
        criarLivro("Padrões de Projeto", "9788573076103", "Bookman", 2000, tecnologia, Set.of(gamma, helm));
    }

    private Livro criarLivro(String titulo, String isbn, String editora, int ano,
                             Categoria categoria, Set<Autor> autores) {
        Livro livro = new Livro();
        livro.setTitulo(titulo);
        livro.setIsbn(isbn);
        livro.setEditora(editora);
        livro.setAnoPublicacao(ano);
        livro.setCategoria(categoria);
        livro.setAutores(new java.util.HashSet<>(autores));
        return livroRepository.save(livro);
    }
}
