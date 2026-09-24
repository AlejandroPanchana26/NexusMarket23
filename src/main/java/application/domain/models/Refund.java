package application.domain.models;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.exceptions.InvalidStatusTransitionException;
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

    // No se puede reembolsar más de lo que se pagó por el pedido.
    public void createFor(Return approvedReturn, BigDecimal refundAmount) {
        if (approvedReturn == null || !approvedReturn.canBeRefunded()) {
            throw new BusinessRuleViolationException("Only approved returns can be refunded.");
        }
        if (refundAmount == null || refundAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleViolationException("Refund amount must be greater than zero.");
        }
        if (refundAmount.compareTo(approvedReturn.getOrder().getTotal()) > 0) {
            throw new BusinessRuleViolationException("Refund amount cannot exceed the order total.");
        }
        this.returnRequest = approvedReturn;
        this.amount = refundAmount;
        this.status = RefundStatus.PENDING;
        this.date = LocalDateTime.now();
    }

    public void process() {
        requirePending(RefundStatus.PROCESSED);
        this.status = RefundStatus.PROCESSED;
        this.date = LocalDateTime.now();
    }

    public void reject() {
        requirePending(RefundStatus.REJECTED);
        this.status = RefundStatus.REJECTED;
    }

    private void requirePending(RefundStatus target) {
        if (!RefundStatus.PENDING.equals(status)) {
            throw new InvalidStatusTransitionException("Refund", status, target);
        }
    }
}
