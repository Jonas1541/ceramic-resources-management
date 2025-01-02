package com.jonasdurau.ceramicmanagement.controllers.exceptions;

public class ResourceDeletionException extends RuntimeException {
    
    public ResourceDeletionException(String message) {
        super(message);
    }
}
