package application.domain.models;

import application.domain.exceptions.BusinessRuleViolationException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class OrderDetail {

    private Product product;
    private Integer quantity;

    // Precio unitario acordado en el pedido.
    private BigDecimal unitPrice;

    // Subtotal de la línea, cantidad por precio unitario.
    private BigDecimal subtotal;

    public OrderDetail(Product product, int quantity, BigDecimal unitPrice) {
        if (product == null || unitPrice == null) {
            throw new BusinessRuleViolationException("Order line requires a product and a price.");
        }
        if (quantity <= 0) {
            throw new BusinessRuleViolationException("Quantity must be greater than zero.");
        }
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
