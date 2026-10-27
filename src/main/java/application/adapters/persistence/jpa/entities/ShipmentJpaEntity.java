package application.adapters.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "shipments")
@Getter
@Setter
@NoArgsConstructor
public class ShipmentJpaEntity {

    @Id
    @Column(name = "identifier", length = 60)
    private String identifier;

    // Un pedido tiene un solo envío.
    @Column(name = "order_identifier", nullable = false, unique = true, length = 60)
    private String orderIdentifier;

    @Column(name = "origin_warehouse_identifier", nullable = false, length = 60)
    private String originWarehouseIdentifier;

    @Column(name = "destination_address", nullable = false, length = 200)
    private String destinationAddress;

    @Column(name = "status", length = 40)
    private String status;

    // Se guarda el nombre de usuario del operador logístico.
    @Column(name = "logistics_operator_username", nullable = false, length = 80)
    private String logisticsOperatorUsername;

    @Column(name = "shipment_date")
    private LocalDateTime shipmentDate;

    @Column(name = "delivery_date")
    private LocalDateTime deliveryDate;
}
