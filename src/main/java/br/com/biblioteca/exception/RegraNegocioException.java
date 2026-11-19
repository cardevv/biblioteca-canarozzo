package br.com.biblioteca.exception;

// Lancada quando uma regra de negocio e violada (vira HTTP 400)
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
