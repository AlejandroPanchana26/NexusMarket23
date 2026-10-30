package application.adapters.rest.dtos.responses;

import java.time.LocalDateTime;

public record ReturnResponse(String identifier, String orderIdentifier, String reason, String status,
                             LocalDateTime requestDate) {
}
