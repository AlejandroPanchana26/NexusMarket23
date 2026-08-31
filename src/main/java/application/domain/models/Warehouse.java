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

    // Tipo de bodega ya sea del marketplace o de un vendedor.
    private WarehouseType type;

  // Si es bodega de un vendedor, aquí va ese vendedor, si es del marketplace, queda vacío
    private Seller owner;
}
