package com.Package.ExpenseTracker.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Handles validation errors (@Valid failures)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        List<Map<String, String>> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> {
                    Map<String, String> errorDetail = new HashMap<>();
                    errorDetail.put("field", error.getField());
                    errorDetail.put("message", error.getDefaultMessage());
                    errorDetail.put("rejected", String.valueOf(error.getRejectedValue()));
                    return errorDetail;
                })
                .collect(Collectors.toList());

        log.warn("Validation failed: {}", fieldErrors);

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Failed");
        response.put("details", fieldErrors);

        return ResponseEntity.badRequest().body(response);
    }

    // Handles transaction not found
    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleTransactionNotFound(
            TransactionNotFoundException ex) {

        log.warn("Transaction not found: {}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("error", "Not Found");
        response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Handles wrong path variable types (e.g., /api/transactions/abc instead of /api/transactions/1)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex) {

        log.warn("Type mismatch: parameter '{}' value '{}' is not valid",
                ex.getName(), ex.getValue());

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Bad Request");
        response.put("message", "Invalid value '" + ex.getValue()
                + "' for parameter '" + ex.getName() + "'");

        return ResponseEntity.badRequest().body(response);
    }

    // Handles invalid JSON body
    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidJson(
            org.springframework.http.converter.HttpMessageNotReadableException ex) {

        log.warn("Invalid request body: {}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Bad Request");
        response.put("message", "Invalid request body. Please check your JSON format.");

        return ResponseEntity.badRequest().body(response);
    }

    // Catches everything else
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {

        log.error("Unexpected error occurred", ex);

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", "Internal Server Error");
        response.put("message", "Something went wrong. Please try again later.");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // Add this method inside the existing class
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {

        log.warn("Runtime exception: {}", ex.getMessage());

        // Check if it's an auth error
        if (ex.getMessage() != null && ex.getMessage().contains("Not authenticated")) {
            Map<String, Object> response = new HashMap<>();
            response.put("timestamp", LocalDateTime.now().toString());
            response.put("status", HttpStatus.UNAUTHORIZED.value());
            response.put("error", "Unauthorized");
            response.put("message", "Please login first");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Bad Request");
        response.put("message", ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }
}