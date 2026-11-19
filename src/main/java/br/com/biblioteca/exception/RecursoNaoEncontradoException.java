package br.com.biblioteca.exception;

// Lancada quando um registro buscado nao existe (vira HTTP 404)
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
