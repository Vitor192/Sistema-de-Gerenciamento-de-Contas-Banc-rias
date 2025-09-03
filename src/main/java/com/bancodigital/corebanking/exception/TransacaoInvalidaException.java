package com.bancodigital.corebanking.exception;

public class TransacaoInvalidaException extends RuntimeException {
    
    public TransacaoInvalidaException(String message) {
        super(message);
    }
    
    public TransacaoInvalidaException(String message, Throwable cause) {
        super(message, cause);
    }
}