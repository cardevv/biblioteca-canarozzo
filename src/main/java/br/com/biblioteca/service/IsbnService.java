package br.com.biblioteca.service;

import br.com.biblioteca.client.IsbnClient;
import br.com.biblioteca.dto.IsbnResponse;
import org.springframework.stereotype.Service;

// Camada de servico da consulta de ISBN: o controller nao fala direto com o client
@Service
public class IsbnService {

    private final IsbnClient isbnClient;

    public IsbnService(IsbnClient isbnClient) {
        this.isbnClient = isbnClient;
    }

    public IsbnResponse consultar(String isbn) {
        return isbnClient.buscarPorIsbn(isbn);
    }
}
