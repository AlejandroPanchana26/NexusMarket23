package application.adapters.rest.dtos.requests;

// Si no se indica dirección, se usa la dirección principal del comprador.
public record PrepareShipmentRequest(String destinationAddress) {
}
