package application.domain.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class CartItem {

    private Product product;
    private Integer quantity;

    // Precio del producto al momento de agregarlo al carrito.
    private BigDecimal unitPrice;
}
