package application.domain.models;

import application.domain.valueobjects.SellerStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Seller extends Person {

    private SellerStatus sellerStatus;

    // Bodegas del vendedor. Se inicializa vacía; se carga cuando se consultan.
    private List<Warehouse> warehouses = new ArrayList<>();

    // Productos publicados por el vendedor. Se inicializa vacía; se carga cuando se consultan.
    private List<Product> products = new ArrayList<>();
}
