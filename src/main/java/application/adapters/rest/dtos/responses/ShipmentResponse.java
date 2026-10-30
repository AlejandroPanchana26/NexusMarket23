package application.adapters.rest.dtos.responses;

import java.time.LocalDateTime;

public record ShipmentResponse(String identifier, String orderIdentifier, String originWarehouseName,
                               String destinationAddress, String status, String logisticsOperatorUsername,
                               LocalDateTime shipmentDate, LocalDateTime deliveryDate) {
}
