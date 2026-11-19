package br.com.biblioteca.service;

import br.com.biblioteca.dto.AutorRequest;
import br.com.biblioteca.dto.AutorResponse;
import br.com.biblioteca.exception.RecursoNaoEncontradoException;
import br.com.biblioteca.model.Autor;
import br.com.biblioteca.repository.AutorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AutorService {

    private final AutorRepository autorRepository;

    public AutorService(AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
    }

    public List<AutorResponse> listar() {
        return autorRepository.findAll().stream().map(AutorResponse::de).toList();
    }

    public AutorResponse buscar(Long id) {
        return AutorResponse.de(buscarEntidade(id));
    }

    public Autor buscarEntidade(Long id) {
        return autorRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Autor não encontrado: " + id));
    }

    @Transactional
    public AutorResponse criar(AutorRequest dados) {
        Autor autor = new Autor(dados.nome(), dados.nacionalidade());
        return AutorResponse.de(autorRepository.save(autor));
    }

    @Transactional
    public AutorResponse atualizar(Long id, AutorRequest dados) {
        Autor autor = buscarEntidade(id);
        autor.setNome(dados.nome());
        autor.setNacionalidade(dados.nacionalidade());
        return AutorResponse.de(autor);
    }

    @Transactional
    public void excluir(Long id) {
        Autor autor = buscarEntidade(id);
        autorRepository.delete(autor);
    }
}
