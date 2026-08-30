package com.homework.library.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BookInvalidStateException.class)
    public ResponseEntity<Map<String, String>> handleBookInvalidState(
            BookInvalidStateException exception) {

        return createResponseEntity(HttpStatus.CONFLICT, exception);
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleBookNotFound(
            BookNotFoundException exception) {

        return createResponseEntity(HttpStatus.NOT_FOUND, exception);
    }

    @ExceptionHandler(BorrowerNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleBorrowerNotFound(
            BorrowerNotFoundException exception
    ) {
        return createResponseEntity(HttpStatus.NOT_FOUND, exception);
    }

    private ResponseEntity<Map<String, String>> createResponseEntity(HttpStatus status, RuntimeException exception) {
        return ResponseEntity
                .status(status)
                .body(Map.of("message", exception.getMessage()));
    }
}
