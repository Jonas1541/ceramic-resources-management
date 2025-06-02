package com.jonasdurau.ceramicmanagement.controllers.exceptions;

public class CleanupJobException extends RuntimeException {
    public CleanupJobException(String message, Throwable cause) {
        super(message, cause);
    }
}
