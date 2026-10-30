package application.adapters.rest.dtos.responses;

public record InventoryResponse(String identifier, String productIdentifier, String productName,
                                String warehouseIdentifier, String warehouseName,
                                Integer availableQuantity, Integer reservedQuantity, String status) {
}
