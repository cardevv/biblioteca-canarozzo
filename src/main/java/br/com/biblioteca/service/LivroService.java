package br.com.biblioteca.service;

import br.com.biblioteca.dto.LivroRequest;
import br.com.biblioteca.dto.LivroResponse;
import br.com.biblioteca.exception.RecursoNaoEncontradoException;
import br.com.biblioteca.exception.RegraNegocioException;
import br.com.biblioteca.model.Autor;
import br.com.biblioteca.model.Livro;
import br.com.biblioteca.repository.AutorRepository;
import br.com.biblioteca.repository.ExemplarRepository;
import br.com.biblioteca.repository.LivroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
public class LivroService {

    private final LivroRepository livroRepository;
    private final AutorRepository autorRepository;
    private final CategoriaService categoriaService;
    private final ExemplarRepository exemplarRepository;

    public LivroService(LivroRepository livroRepository, AutorRepository autorRepository,
                        CategoriaService categoriaService, ExemplarRepository exemplarRepository) {
        this.livroRepository = livroRepository;
        this.autorRepository = autorRepository;
        this.categoriaService = categoriaService;
        this.exemplarRepository = exemplarRepository;
    }

    // readOnly: so leitura; a transacao fica aberta enquanto montamos o DTO
    // (os autores sao carregados sob demanda - LAZY)
    @Transactional(readOnly = true)
    public List<LivroResponse> listar(String titulo) {
        List<Livro> livros;
        if (titulo == null || titulo.isBlank()) {
            livros = livroRepository.findAllByOrderByTitulo();
        } else {
            livros = livroRepository.findByTituloContainingIgnoreCaseOrderByTitulo(titulo.trim());
        }
        return livros.stream().map(LivroResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public LivroResponse buscar(Long id) {
        return LivroResponse.de(buscarEntidade(id));
    }

    public Livro buscarEntidade(Long id) {
        return livroRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Livro não encontrado: " + id));
    }

    @Transactional
    public LivroResponse criar(LivroRequest dados) {
        validarIsbnUnico(dados.isbn(), null);
        Livro livro = new Livro();
        preencher(livro, dados);
        return LivroResponse.de(livroRepository.save(livro));
    }

    @Transactional
    public LivroResponse atualizar(Long id, LivroRequest dados) {
        Livro livro = buscarEntidade(id);
        validarIsbnUnico(dados.isbn(), id);
        preencher(livro, dados);
        return LivroResponse.de(livro);
    }

    @Transactional
    public void excluir(Long id) {
        Livro livro = buscarEntidade(id);
        // Regra: nao excluir livro que tenha exemplares
        if (exemplarRepository.existsByLivroId(id)) {
            throw new RegraNegocioException("Não é possível excluir: o livro possui exemplares");
        }
        livroRepository.delete(livro);
    }

    // Copia os dados do DTO para a entidade, buscando categoria e autores pelos IDs
    private void preencher(Livro livro, LivroRequest dados) {
        livro.setTitulo(dados.titulo());
        livro.setIsbn(dados.isbn().trim());
        livro.setEditora(dados.editora());
        livro.setAnoPublicacao(dados.anoPublicacao());
        livro.setCategoria(categoriaService.buscarEntidade(dados.categoriaId()));

        List<Autor> autores = autorRepository.findAllById(dados.autorIds());
        if (autores.size() != new HashSet<>(dados.autorIds()).size()) {
            throw new RecursoNaoEncontradoException("Um ou mais autores não foram encontrados");
        }
        livro.setAutores(new HashSet<>(autores));
    }

    // Regra: ISBN unico (outro livro nao pode ter o mesmo)
    private void validarIsbnUnico(String isbn, Long idAtual) {
        livroRepository.findByIsbn(isbn.trim()).ifPresent(existente -> {
            if (!existente.getId().equals(idAtual)) {
                throw new RegraNegocioException("Já existe livro com o ISBN " + isbn);
            }
        });
    }
}
