package br.com.estacionamento.Sistema.de.Estacionamento.exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}