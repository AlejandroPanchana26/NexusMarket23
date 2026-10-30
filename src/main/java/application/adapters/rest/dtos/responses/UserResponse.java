package application.adapters.rest.dtos.responses;

// Datos del usuario que devuelve la API. No incluye la contraseña.
public record UserResponse(
        Integer userId,
        String username,
        String status,
        String role,
        String identification,
        String name,
        String email) {
}
