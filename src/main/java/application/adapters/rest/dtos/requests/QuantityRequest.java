package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

// Cantidad para sumar existencias o para fijarlas tras un conteo físico.
public record QuantityRequest(@NotNull @PositiveOrZero Integer quantity) {
}
