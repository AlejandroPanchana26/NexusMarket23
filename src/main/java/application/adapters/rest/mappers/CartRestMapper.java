package application.adapters.rest.mappers;

import application.adapters.rest.dtos.responses.CartItemResponse;
import application.adapters.rest.dtos.responses.CartResponse;
import application.domain.models.CartItem;
import application.domain.models.ShoppingCart;

import java.util.ArrayList;
import java.util.List;

public final class CartRestMapper {

    private CartRestMapper() {
    }

    public static CartResponse toResponse(ShoppingCart cart) {
        List<CartItemResponse> items = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            items.add(new CartItemResponse(
                    item.getProduct().getIdentifier(),
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getUnitPrice(),
                    item.calculateSubtotal()));
        }
        return new CartResponse(cart.getIdentifier(), items, cart.calculateTotal());
    }
}
