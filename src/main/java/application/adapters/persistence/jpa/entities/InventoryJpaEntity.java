package application.adapters.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Un producto solo puede tener un registro de inventario por bodega.
@Entity
@Table(name = "inventories",
        uniqueConstraints = @UniqueConstraint(columnNames = {"product_identifier", "warehouse_identifier"}))
@Getter
@Setter
@NoArgsConstructor
public class InventoryJpaEntity {

    @Id
    @Column(name = "identifier", length = 60)
    private String identifier;

    @Column(name = "product_identifier", nullable = false, length = 60)
    private String productIdentifier;

    @Column(name = "warehouse_identifier", nullable = false, length = 60)
    private String warehouseIdentifier;

    @Column(name = "available_quantity", nullable = false)
    private Integer availableQuantity;

    @Column(name = "reserved_quantity", nullable = false)
    private Integer reservedQuantity;

    @Column(name = "status", length = 40)
    private String status;
}
