package application.domain.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}
