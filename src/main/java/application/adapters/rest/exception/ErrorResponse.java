package application.adapters.rest.exception;

import java.time.LocalDateTime;

// Forma de todas las respuestas de error de la API.
public record ErrorResponse(int status, String error, String message, LocalDateTime timestamp) {
}
