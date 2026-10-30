package application.adapters.rest.dtos.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RefundResponse(String identifier, String returnIdentifier, BigDecimal amount, String status,
                             LocalDateTime date) {
}
