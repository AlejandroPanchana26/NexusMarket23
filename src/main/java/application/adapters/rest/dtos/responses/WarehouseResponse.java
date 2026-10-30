package application.adapters.rest.dtos.responses;

// ownerIdentification y ownerName vienen vacíos en las bodegas del marketplace.
public record WarehouseResponse(String identifier, String name, String address, String type,
                                String ownerIdentification, String ownerName) {
}
