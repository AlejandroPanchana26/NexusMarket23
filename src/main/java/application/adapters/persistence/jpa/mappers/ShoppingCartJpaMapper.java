package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.CartLineEmbeddable;
import application.adapters.persistence.jpa.entities.ShoppingCartJpaEntity;
import application.domain.models.Buyer;
import application.domain.models.CartItem;
import application.domain.models.Product;
import application.domain.models.ShoppingCart;

public final class ShoppingCartJpaMapper {

    private ShoppingCartJpaMapper() {
    }

    public static ShoppingCartJpaEntity toEntity(ShoppingCart cart) {
        ShoppingCartJpaEntity entity = new ShoppingCartJpaEntity();
        entity.setIdentifier(cart.getIdentifier());
        entity.setBuyerIdentification(cart.getBuyer().getIdentification());
        for (CartItem item : cart.getItems()) {
            CartLineEmbeddable line = new CartLineEmbeddable();
            line.setProductIdentifier(item.getProduct().getIdentifier());
            line.setQuantity(item.getQuantity());
            line.setUnitPrice(item.getUnitPrice());
            entity.getItems().add(line);
        }
        return entity;
    }

    // Las líneas se agregan después desde el adaptador, cuando ya tiene cada producto.
    public static ShoppingCart toDomain(ShoppingCartJpaEntity entity, Buyer buyer) {
        ShoppingCart cart = new ShoppingCart();
        cart.setIdentifier(entity.getIdentifier());
        cart.setBuyer(buyer);
        return cart;
    }

    // No se usa el constructor de CartItem porque copiaría el precio actual; se conserva el guardado.
    public static CartItem toDomainItem(CartLineEmbeddable line, Product product) {
        CartItem item = new CartItem();
        item.setProduct(product);
        item.setQuantity(line.getQuantity());
        item.setUnitPrice(line.getUnitPrice());
        return item;
    }
}
