package com.jonasdurau.ceramicmanagement.controllers.exceptions;

public class ExpiredTokenException extends RuntimeException {
    
    public ExpiredTokenException(String message) {
        super(message);
    }
}
