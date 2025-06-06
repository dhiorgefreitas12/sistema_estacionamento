package br.com.estacionamento.Sistema.de.Estacionamento.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}