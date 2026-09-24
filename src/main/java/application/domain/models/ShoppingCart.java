package application.domain.models;

import application.domain.exceptions.BusinessRuleViolationException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ShoppingCart {

    private String identifier;

    // Comprador dueño del carrito.
    private Buyer buyer;

    // Productos seleccionados provisionalmente. Vacía por defecto.
    private List<CartItem> items = new ArrayList<>();

    // Si el producto ya está en el carrito se suma la cantidad en lugar de repetir la línea.
    public void addItem(Product product, int quantity) {
        if (buyer == null || !buyer.canPlaceOrders()) {
            throw new BusinessRuleViolationException("Buyer is not enabled to purchase.");
        }
        if (product == null || !product.isAvailableForSale()) {
            throw new BusinessRuleViolationException("Product is not available for sale.");
        }
        CartItem existing = findItem(product);
        if (existing != null) {
            existing.increaseQuantity(quantity);
        } else {
            items.add(new CartItem(product, quantity));
        }
    }

    public void removeItem(Product product) {
        CartItem existing = findItem(product);
        if (existing == null) {
            throw new BusinessRuleViolationException("Product is not in the cart.");
        }
        items.remove(existing);
    }

    public void clear() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public BigDecimal calculateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : items) {
            total = total.add(item.calculateSubtotal());
        }
        return total;
    }

    private CartItem findItem(Product product) {
        for (CartItem item : items) {
            if (item.isForProduct(product)) {
                return item;
            }
        }
        return null;
    }
}
