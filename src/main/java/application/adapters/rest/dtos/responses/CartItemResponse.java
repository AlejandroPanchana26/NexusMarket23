package application.adapters.rest.dtos.responses;

import java.math.BigDecimal;

public record CartItemResponse(String productIdentifier, String productName, Integer quantity,
                               BigDecimal unitPrice, BigDecimal subtotal) {
}
