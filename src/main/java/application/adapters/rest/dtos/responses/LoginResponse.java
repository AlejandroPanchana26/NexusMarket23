package application.adapters.rest.dtos.responses;

// Respuesta del login: el token que se debe enviar en cada petición como "Authorization: Bearer <token>".
public record LoginResponse(String token, String tokenType, long expiresInMinutes, UserResponse user) {
}
