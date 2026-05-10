package org.example.cvgenerator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDateTime;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Map<String, Object>> handleClientError(
            HttpClientErrorException ex) {

        String message = switch (ex.getStatusCode().value()) {
            case 401 -> "Invalid API key. Please check your OpenRouter configuration.";
            case 429 -> "AI service is rate limited. Please try again in a moment.";
            case 404 -> "AI model not found. Please check your model configuration.";
            default -> "Client error when calling AI service: " + ex.getMessage();
        };

        return buildResponse(HttpStatus.BAD_GATEWAY, message);
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<Map<String, Object>> handleServerError(
            HttpServerErrorException ex) {
        return buildResponse(
                HttpStatus.BAD_GATEWAY,
                "AI service is currently unavailable. Please try again later."
        );
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<Map<String, Object>> handleConnectionError(
            ResourceAccessException ex) {
        return buildResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Cannot reach AI service. Please check your internet connection."
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericError(
            Exception ex) {
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong. Please try again."
        );
    }

    private ResponseEntity<Map<String, Object>> buildResponse(
            HttpStatus status, String message) {
        return ResponseEntity
                .status(status)
                .body(Map.of(
                        "timestamp", LocalDateTime.now().toString(),
                        "status", status.value(),
                        "error", message
                ));
    }
}