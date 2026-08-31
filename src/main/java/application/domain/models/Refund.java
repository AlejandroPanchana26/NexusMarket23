package application.domain.models;

import application.domain.valueobjects.RefundStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Refund {

    private String identifier;

    // Devolución que origina el reembolso.
    private Return returnRequest;

    // Monto a reembolsar al comprador.
    private BigDecimal amount;

    private RefundStatus status;

    private LocalDateTime date;
}
