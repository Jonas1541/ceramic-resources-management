package com.jonasdurau.ceramicmanagement.controllers.exceptions;

public class InvalidCredentialsException extends RuntimeException {
    
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
