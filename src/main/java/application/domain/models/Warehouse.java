package application.domain.models;

import application.domain.valueobjects.WarehouseType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Warehouse {

    private String identifier;
    private String name;
    private String address;

    // Tipo de bodega: del Marketplace o de un vendedor.
    private WarehouseType type;

    // Vendedor propietario. Es nulo cuando la bodega pertenece al Marketplace.
    private Seller owner;
}
