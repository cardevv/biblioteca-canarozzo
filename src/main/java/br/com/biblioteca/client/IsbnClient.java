package br.com.biblioteca.client;

import br.com.biblioteca.dto.IsbnResponse;
import br.com.biblioteca.exception.RecursoNaoEncontradoException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

// Cliente HTTP da API externa BrasilAPI, que consulta dados de um livro pelo ISBN.
// Usa o RestClient do proprio Spring (sem bibliotecas extras).
@Component
public class IsbnClient {

    private final RestClient restClient;

    public IsbnClient(RestClient.Builder builder) {
        this.restClient = builder.baseUrl("https://brasilapi.com.br/api/isbn/v1").build();
    }

    public IsbnResponse buscarPorIsbn(String isbn) {
        // Deixa so os digitos (aceita "978-85-359-1484-9")
        String somenteDigitos = isbn.replaceAll("[^0-9Xx]", "");
        try {
            // GET https://brasilapi.com.br/api/isbn/v1/{isbn} e converte o JSON no record
            IsbnResponse resposta = restClient.get()
                    .uri("/{isbn}", somenteDigitos)
                    .retrieve()
                    .body(IsbnResponse.class);

            if (resposta == null || resposta.title() == null) {
                throw new RecursoNaoEncontradoException("ISBN não encontrado: " + isbn);
            }
            return resposta;
        } catch (RestClientException e) {
            // 4xx (ISBN inexistente/invalido), 5xx ou falha de rede
            throw new RecursoNaoEncontradoException("Não foi possível encontrar o ISBN " + isbn + " na BrasilAPI");
        }
    }
}
