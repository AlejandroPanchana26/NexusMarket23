package application.adapters.rest.dtos.responses;

// Estado comercial de un vendedor o comprador después de un cambio hecho por el administrador.
public record PersonStatusResponse(String identification, String name, String role, String status) {
}
