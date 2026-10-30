package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record RegisterInventoryRequest(@NotBlank String productIdentifier, @NotBlank String warehouseIdentifier,
                                       @NotNull @PositiveOrZero Integer initialQuantity) {
}
