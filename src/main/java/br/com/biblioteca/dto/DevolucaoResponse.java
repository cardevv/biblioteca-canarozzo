package br.com.biblioteca.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

// Resultado da devolucao. diasAtraso e valorMulta sao calculados na hora
// (nao sao guardados em tabela).
public record DevolucaoResponse(
        Long emprestimoId,
        String livroTitulo,
        String exemplarCodigo,
        LocalDate dataPrevistaDevolucao,
        LocalDate dataDevolucao,
        long diasAtraso,
        BigDecimal valorMulta
) {
}
