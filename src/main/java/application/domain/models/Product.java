package application.domain.models;

import application.domain.enums.ProductType;
import application.domain.valueobjects.ProductStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class Product {

    // Identificador único del producto dentro del catálogo.
    private String identifier;
    private String name;
    private String description;

    // Se usa BigDecimal para el precio porque es el tipo adecuado para valores monetarios.
    private BigDecimal price;

    // Vendedor propietario del producto.
    private Seller seller;

    // Variantes del producto (color, talla, modelo, etc.). Vacía por defecto.
    private List<String> variants = new ArrayList<>();

    private ProductStatus status;

    // Tipo de producto: físico o digital.
    private ProductType type;
}
