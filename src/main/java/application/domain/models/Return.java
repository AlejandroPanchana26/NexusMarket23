package application.domain.models;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.valueobjects.ReturnStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Return {

    private String identifier;

    // Pedido sobre el cual se solicita la devolución.
    private Order order;

    // Motivo de la devolución.
    private String reason;

    private ReturnStatus status;

    private LocalDateTime requestDate;

    // Solo se devuelve un pedido ya entregado; el pedido no se modifica, solo se referencia.
    public void requestFor(Order deliveredOrder, String returnReason) {
        if (deliveredOrder == null || !deliveredOrder.isFinalized()) {
            throw new BusinessRuleViolationException("Only delivered orders can be returned.");
        }
        if (returnReason == null || returnReason.isBlank()) {
            throw new BusinessRuleViolationException("Return reason is required.");
        }
        this.order = deliveredOrder;
        this.reason = returnReason;
        this.status = ReturnStatus.REQUESTED;
        this.requestDate = LocalDateTime.now();
    }

    public void approve() {
        requireStatus(ReturnStatus.REQUESTED, ReturnStatus.APPROVED);
        this.status = ReturnStatus.APPROVED;
    }

    public void reject() {
        requireStatus(ReturnStatus.REQUESTED, ReturnStatus.REJECTED);
        this.status = ReturnStatus.REJECTED;
    }

    public void complete() {
        requireStatus(ReturnStatus.APPROVED, ReturnStatus.COMPLETED);
        this.status = ReturnStatus.COMPLETED;
    }

    public boolean canBeRefunded() {
        return ReturnStatus.APPROVED.equals(status) || ReturnStatus.COMPLETED.equals(status);
    }

    private void requireStatus(ReturnStatus expected, ReturnStatus target) {
        if (!expected.equals(status)) {
            throw new InvalidStatusTransitionException("Return", status, target);
        }
    }
}
