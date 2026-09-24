package application.domain.models;

import application.domain.exceptions.BusinessRuleViolationException;
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

    public CartItem(Product product, int quantity) {
        if (product == null) {
            throw new BusinessRuleViolationException("Product is required.");
        }
        requirePositive(quantity);
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = product.getPrice();
    }

    public void increaseQuantity(int extra) {
        requirePositive(extra);
        this.quantity += extra;
    }

    public BigDecimal calculateSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    // Dos productos son el mismo si comparten identificador.
    public boolean isForProduct(Product other) {
        if (other == null) {
            return false;
        }
        if (product == other) {
            return true;
        }
        return product.getIdentifier() != null && product.getIdentifier().equals(other.getIdentifier());
    }

    private static void requirePositive(int value) {
        if (value <= 0) {
            throw new BusinessRuleViolationException("Quantity must be greater than zero.");
        }
    }
}
