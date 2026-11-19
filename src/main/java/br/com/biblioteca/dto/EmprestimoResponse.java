package br.com.biblioteca.dto;

import br.com.biblioteca.model.Emprestimo;
import br.com.biblioteca.model.StatusEmprestimo;

import java.time.LocalDate;

public record EmprestimoResponse(
        Long id,
        Long usuarioId,
        String usuarioNome,
        Long exemplarId,
        String exemplarCodigo,
        String livroTitulo,
        LocalDate dataEmprestimo,
        LocalDate dataPrevistaDevolucao,
        LocalDate dataDevolucao,
        StatusEmprestimo status
) {

    public static EmprestimoResponse de(Emprestimo e) {
        return new EmprestimoResponse(
                e.getId(),
                e.getUsuario().getId(),
                e.getUsuario().getNome(),
                e.getExemplar().getId(),
                e.getExemplar().getCodigo(),
                e.getExemplar().getLivro().getTitulo(),
                e.getDataEmprestimo(),
                e.getDataPrevistaDevolucao(),
                e.getDataDevolucao(),
                e.getStatus()
        );
    }
}
