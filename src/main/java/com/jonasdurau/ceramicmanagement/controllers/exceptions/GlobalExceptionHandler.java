package com.jonasdurau.ceramicmanagement.controllers.exceptions;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Validações de campos (Bean Validation)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        // Extrai os erros de validação em um Map<field, message>
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        // Montamos uma mensagem concatenada ou algo personalizado.
        // Aqui, só um join simples:
        String combinedErrors = errors.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining("; "));
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError(Instant.now(), status.value(), "Validation Exception", combinedErrors, request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    // Exceções de negócio em geral
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<StandardError> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT; // 409
        StandardError err = new StandardError(Instant.now(), status.value(), "Business Exception", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    // Exceções de credenciais inválidas (login)
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<StandardError> handleInvalidCredentials(InvalidCredentialsException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED; // 401
        StandardError err = new StandardError(Instant.now(), status.value(), "Invalid Credentials", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError err = new StandardError(Instant.now(), status.value(), "Resource Not Found", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

}
