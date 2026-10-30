package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.NotBlank;

// Sin sellerIdentification se crea una bodega del marketplace.
public record RegisterWarehouseRequest(@NotBlank String name, @NotBlank String address, String sellerIdentification) {
}
