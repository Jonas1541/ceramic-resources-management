package com.jonasdurau.ceramicmanagement.controllers.exceptions;

public class ResourceNotFoundException extends RuntimeException{
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
