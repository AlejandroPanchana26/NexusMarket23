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

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shopping_carts")
@Getter
@Setter
@NoArgsConstructor
public class ShoppingCartJpaEntity {

    @Id
    @Column(name = "identifier", length = 60)
    private String identifier;

    // Cada comprador tiene un solo carrito.
    @Column(name = "buyer_identification", nullable = false, unique = true, length = 60)
    private String buyerIdentification;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "cart_items", joinColumns = @JoinColumn(name = "cart_identifier"))
    private List<CartLineEmbeddable> items = new ArrayList<>();
}
