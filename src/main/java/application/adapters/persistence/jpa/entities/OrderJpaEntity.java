package application.adapters.persistence.jpa.entities;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class OrderJpaEntity {

    @Id
    @Column(name = "identifier", length = 60)
    private String identifier;

    @Column(name = "buyer_identification", nullable = false, length = 60)
    private String buyerIdentification;

    @Column(name = "status", length = 40)
    private String status;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "total", precision = 15, scale = 2)
    private BigDecimal total;

    // Vacío si el pedido es solo digital.
    @Column(name = "fulfillment_warehouse_identifier", length = 60)
    private String fulfillmentWarehouseIdentifier;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "order_details", joinColumns = @JoinColumn(name = "order_identifier"))
    private List<OrderLineEmbeddable> details = new ArrayList<>();
}
