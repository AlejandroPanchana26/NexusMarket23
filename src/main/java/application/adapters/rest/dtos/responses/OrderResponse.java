package application.adapters.rest.dtos.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(String identifier, String status, LocalDateTime creationDate, BigDecimal total,
                            String buyerIdentification, String fulfillmentWarehouseName, List<OrderLineResponse> lines) {
}
