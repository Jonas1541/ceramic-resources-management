package com.jonasdurau.ceramicmanagement.controllers.exceptions;

public class InvalidTokenException extends RuntimeException {
    
    public InvalidTokenException(String message) {
        super(message);
    }
}
