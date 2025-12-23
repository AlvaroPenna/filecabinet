package com.filecabinet.filecabinet.config;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalStateException(IllegalStateException ex) {
        Map<String, String> errorResponse = Map.of("error", ex.getMessage());
        
        // Devolvemos la respuesta con el código de estado 409 (Conflict)
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
    
}
