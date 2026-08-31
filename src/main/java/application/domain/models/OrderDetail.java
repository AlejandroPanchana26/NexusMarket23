package application.domain.models;

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
}
