package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// El administrador registra al vendedor junto con su primera bodega.
public record RegisterSellerRequest(
        @NotBlank String identification,
        @NotBlank String name,
        @NotBlank @Email String email,
        String phoneNumber,
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank String warehouseName,
        @NotBlank String warehouseAddress) {
}
