package application.adapters.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

// Línea del carrito. No es una tabla propia: se guarda dentro de cart_items.
@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class CartLineEmbeddable {

    @Column(name = "product_identifier", nullable = false, length = 60)
    private String productIdentifier;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", precision = 15, scale = 2)
    private BigDecimal unitPrice;
}
