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
import java.util.ArrayList;
import java.util.List;

// Tabla única para productos físicos y digitales; productType indica cuál es cada fila.
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class ProductJpaEntity {

    @Id
    @Column(name = "identifier", length = 60)
    private String identifier;

    // PHYSICAL o DIGITAL.
    @Column(name = "product_type", nullable = false, length = 20)
    private String productType;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "price", precision = 15, scale = 2)
    private BigDecimal price;

    @Column(name = "seller_identification", nullable = false, length = 60)
    private String sellerIdentification;

    // Vacío mientras el producto no se haya publicado.
    @Column(name = "status", length = 40)
    private String status;

    // Solo para productos físicos.
    @Column(name = "weight", precision = 10, scale = 3)
    private BigDecimal weight;

    // Solo para productos digitales.
    @Column(name = "download_url", length = 300)
    private String downloadUrl;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_variants", joinColumns = @JoinColumn(name = "product_identifier"))
    @Column(name = "variant", length = 80)
    private List<String> variants = new ArrayList<>();
}
