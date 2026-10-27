package application.adapters.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "warehouses")
@Getter
@Setter
@NoArgsConstructor
public class WarehouseJpaEntity {

    @Id
    @Column(name = "identifier", length = 60)
    private String identifier;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "address", nullable = false, length = 200)
    private String address;

    // MARKETPLACE o SELLER.
    @Column(name = "type", nullable = false, length = 40)
    private String type;

    // Vacío cuando la bodega es del marketplace.
    @Column(name = "owner_identification", length = 60)
    private String ownerIdentification;
}
