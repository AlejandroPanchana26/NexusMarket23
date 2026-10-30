package application.adapters.rest.dtos.responses;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(String cartIdentifier, List<CartItemResponse> items, BigDecimal total) {
}
