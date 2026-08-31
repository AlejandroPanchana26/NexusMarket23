package application.domain.models;

import application.domain.valueobjects.ShipmentStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Shipment {

    private String identifier;

    // Pedido que se está enviando.
    private Order order;

    // Bodega desde donde sale el pedido.
    private Warehouse originWarehouse;

    // Dirección de entrega del comprador.
    private String destinationAddress;

    private ShipmentStatus status;

    // Operador logístico responsable del envío.
    private User logisticsOperator;

    private LocalDateTime shipmentDate;
    private LocalDateTime deliveryDate;
}
