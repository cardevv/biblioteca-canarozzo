package br.com.biblioteca.service;

import br.com.biblioteca.dto.CategoriaRequest;
import br.com.biblioteca.dto.CategoriaResponse;
import br.com.biblioteca.exception.RecursoNaoEncontradoException;
import br.com.biblioteca.exception.RegraNegocioException;
import br.com.biblioteca.model.Categoria;
import br.com.biblioteca.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<CategoriaResponse> listar() {
        return categoriaRepository.findAll().stream().map(CategoriaResponse::de).toList();
    }

    public CategoriaResponse buscar(Long id) {
        return CategoriaResponse.de(buscarEntidade(id));
    }

    public Categoria buscarEntidade(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada: " + id));
    }

    @Transactional
    public CategoriaResponse criar(CategoriaRequest dados) {
        validarNomeUnico(dados.nome(), null);
        return CategoriaResponse.de(categoriaRepository.save(new Categoria(dados.nome())));
    }

    @Transactional
    public CategoriaResponse atualizar(Long id, CategoriaRequest dados) {
        Categoria categoria = buscarEntidade(id);
        validarNomeUnico(dados.nome(), id);
        categoria.setNome(dados.nome());
        return CategoriaResponse.de(categoria);
    }

    @Transactional
    public void excluir(Long id) {
        Categoria categoria = buscarEntidade(id);
        categoriaRepository.delete(categoria);
    }

    // Nome de categoria e unico (ignorando maiusculas/minusculas)
    private void validarNomeUnico(String nome, Long idAtual) {
        categoriaRepository.findByNomeIgnoreCase(nome).ifPresent(existente -> {
            if (!existente.getId().equals(idAtual)) {
                throw new RegraNegocioException("Já existe a categoria " + nome);
            }
        });
    }
}
