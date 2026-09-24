package application.domain.exceptions;

// Excepción base: toda regla de negocio que se incumple en el dominio termina en una de sus hijas.
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }
}
