package com.eventforge.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApiExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException exception, HttpServletRequest request) {
        Map<String, String> fields = new LinkedHashMap<>();
        for (FieldError error : exception.getBindingResult().getFieldErrors()) fields.put(error.getField(), error.getDefaultMessage());
        return response(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Request validation failed", fields, request);
    }
    @ExceptionHandler({HttpMessageNotReadableException.class, ConstraintViolationException.class})
    public ResponseEntity<ErrorResponse> malformed(Exception exception, HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, "MALFORMED_REQUEST", "Request is malformed", Map.of(), request);
    }
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> status(ResponseStatusException exception, HttpServletRequest request) {
        HttpStatus status = HttpStatus.valueOf(exception.getStatusCode().value());
        return response(status, status == HttpStatus.CONFLICT ? "BUSINESS_CONFLICT" : "REQUEST_ERROR", safeMessage(status), Map.of(), request);
    }
    @ExceptionHandler(DomainConflictException.class)
    public ResponseEntity<ErrorResponse> conflict(DomainConflictException exception, HttpServletRequest request) {
        return response(HttpStatus.CONFLICT, "BUSINESS_CONFLICT", "Operation conflicts with current state", Map.of(), request);
    }
    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> auth(AuthenticationFailedException exception, HttpServletRequest request) {
        return response(HttpStatus.UNAUTHORIZED, "UNAUTHENTICATED", "Authentication failed", Map.of(), request);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> unexpected(Exception exception, HttpServletRequest request) {
        String correlationId = correlationId(request);
        LOGGER.error("Unhandled request failure correlationId={}", correlationId, exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred", Instant.now(), correlationId, Map.of()));
    }
    private ResponseEntity<ErrorResponse> response(HttpStatus status, String code, String message, Map<String, String> details, HttpServletRequest request) {
        return ResponseEntity.status(status).body(new ErrorResponse(code, message, Instant.now(), correlationId(request), details));
    }
    private String correlationId(HttpServletRequest request) {
        String id = request.getHeader("X-Request-ID");
        return id == null || id.isBlank() ? UUID.randomUUID().toString() : id;
    }
    private String safeMessage(HttpStatus status) {
        return switch (status) { case NOT_FOUND -> "Requested resource was not found"; case UNAUTHORIZED -> "Authentication is required"; case FORBIDDEN -> "Access is denied"; case CONFLICT -> "Operation conflicts with current state"; default -> "Request cannot be processed"; };
    }
    public record ErrorResponse(String code, String message, Instant timestamp, String correlationId, Map<String, String> details) { }
}
