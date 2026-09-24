package application.domain.models;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.valueobjects.InvoiceStatus;
import application.domain.valueobjects.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Invoice {

    private String identifier;

    // Pedido al que corresponde la factura.
    private Order order;

    private LocalDateTime issueDate;

    // Monto total facturado.
    private BigDecimal totalAmount;

    private InvoiceStatus status;

    // La factura se emite cuando el pedido queda confirmado y a la espera del pago.
    public void issueFor(Order orderToBill) {
        if (orderToBill == null || !OrderStatus.PENDING_PAYMENT.equals(orderToBill.getStatus())) {
            throw new BusinessRuleViolationException("Only orders pending payment can be invoiced.");
        }
        this.order = orderToBill;
        this.totalAmount = orderToBill.getTotal();
        this.issueDate = LocalDateTime.now();
        this.status = InvoiceStatus.ISSUED;
    }

    public void markAsPaid() {
        requireIssued(InvoiceStatus.PAID);
        this.status = InvoiceStatus.PAID;
    }

    // Una factura pagada ya no puede anularse.
    public void cancel() {
        requireIssued(InvoiceStatus.CANCELLED);
        this.status = InvoiceStatus.CANCELLED;
    }

    private void requireIssued(InvoiceStatus target) {
        if (!InvoiceStatus.ISSUED.equals(status)) {
            throw new InvalidStatusTransitionException("Invoice", status, target);
        }
    }
}
