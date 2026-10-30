package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

// Datos que envía una persona para registrarse como compradora. Las anotaciones validan antes de llegar al dominio.
public record RegisterBuyerRequest(
        @NotBlank String identification,
        @NotBlank String name,
        @NotBlank @Email String email,
        String phoneNumber,
        @NotBlank String principalAddress,
        List<String> additionalAddresses,
        @NotBlank String username,
        @NotBlank String password) {
}
