package application.domain.models;

import application.domain.valueobjects.InventoryStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Inventory {

    private String identifier;

    // El inventario aplica solo a productos físicos; los digitales no manejan existencias.
    private PhysicalProduct product;

    // Bodega donde se encuentran físicamente las existencias.
    private Warehouse warehouse;

    // Cantidad disponible para venta. No puede ser negativa (regla del dominio).
    private Integer availableQuantity;

    // Cantidad apartada para pedidos en proceso.
    private Integer reservedQuantity;

    private InventoryStatus status;
}
