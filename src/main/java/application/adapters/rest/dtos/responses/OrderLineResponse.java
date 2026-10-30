package application.adapters.rest.dtos.responses;

import java.math.BigDecimal;

// downloadUrl solo viene en productos digitales de pedidos ya pagados.
public record OrderLineResponse(String productIdentifier, String productName, String productType, Integer quantity,
                                BigDecimal unitPrice, BigDecimal subtotal, String downloadUrl) {
}
