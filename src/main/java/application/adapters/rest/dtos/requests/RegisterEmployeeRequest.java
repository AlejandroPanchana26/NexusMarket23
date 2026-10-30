package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// role es LOGISTICS_OPERATOR, ADMINISTRATOR o SUPERVISOR.
public record RegisterEmployeeRequest(
        @NotBlank String identification,
        @NotBlank String name,
        @NotBlank @Email String email,
        String phoneNumber,
        @NotBlank String role,
        @NotBlank String username,
        @NotBlank String password) {
}
