package application.domain.models;

import application.domain.valueobjects.InvoiceStatus;
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
}
