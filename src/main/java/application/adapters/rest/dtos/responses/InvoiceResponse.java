package application.adapters.rest.dtos.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InvoiceResponse(String identifier, String orderIdentifier, LocalDateTime issueDate,
                              BigDecimal totalAmount, String status) {
}
