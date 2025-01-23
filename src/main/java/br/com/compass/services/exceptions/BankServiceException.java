package br.com.compass.services.exceptions;

public class BankServiceException extends RuntimeException {
    public BankServiceException(String message) {
        super(message);
    }
}

