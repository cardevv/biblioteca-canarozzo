package br.com.biblioteca.dto;

import br.com.biblioteca.model.Exemplar;
import br.com.biblioteca.model.StatusExemplar;

public record ExemplarResponse(Long id, String codigo, StatusExemplar status, Long livroId, String livroTitulo) {

    public static ExemplarResponse de(Exemplar exemplar) {
        return new ExemplarResponse(
                exemplar.getId(),
                exemplar.getCodigo(),
                exemplar.getStatus(),
                exemplar.getLivro().getId(),
                exemplar.getLivro().getTitulo()
        );
    }
}
