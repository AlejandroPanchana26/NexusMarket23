package application.domain.models;

import application.domain.enums.ProductType;
import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.exceptions.InvalidStatusTransitionException;
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


    private BigDecimal price;

    // Vendedor propietario del producto.
    private Seller seller;

    // Variantes del producto color, talla, modelo, demas - Vacía por defecto.
    private List<String> variants = new ArrayList<>();

    private ProductStatus status;

    // Tipo de producto: físico o digital.
    private ProductType type;

    // Cada tipo de producto responde si maneja existencias en bodega.
    public abstract boolean requiresInventory();

    // Un producto nuevo o suspendido puede publicarse; uno descontinuado ya no.
    public void publish() {
        if (ProductStatus.PUBLISHED.equals(status) || ProductStatus.DISCONTINUED.equals(status)) {
            throw new InvalidStatusTransitionException("Product", status, ProductStatus.PUBLISHED);
        }
        if (name == null || name.isBlank()) {
            throw new BusinessRuleViolationException("Product name is required.");
        }
        if (seller == null) {
            throw new BusinessRuleViolationException("Product must belong to a seller.");
        }
        validatePrice(price);
        this.status = ProductStatus.PUBLISHED;
    }

    public void suspend() {
        if (!ProductStatus.PUBLISHED.equals(status)) {
            throw new InvalidStatusTransitionException("Product", status, ProductStatus.SUSPENDED);
        }
        this.status = ProductStatus.SUSPENDED;
    }

    public void discontinue() {
        if (status == null || ProductStatus.DISCONTINUED.equals(status)) {
            throw new InvalidStatusTransitionException("Product", status, ProductStatus.DISCONTINUED);
        }
        this.status = ProductStatus.DISCONTINUED;
    }

    public boolean isAvailableForSale() {
        return ProductStatus.PUBLISHED.equals(status);
    }

    public void changePrice(BigDecimal newPrice) {
        if (ProductStatus.DISCONTINUED.equals(status)) {
            throw new BusinessRuleViolationException("A discontinued product cannot change its price.");
        }
        validatePrice(newPrice);
        this.price = newPrice;
    }

    public void addVariant(String variant) {
        if (variant == null || variant.isBlank()) {
            throw new BusinessRuleViolationException("Variant is required.");
        }
        if (variants.contains(variant)) {
            throw new BusinessRuleViolationException("Variant already exists for this product.");
        }
        variants.add(variant);
    }

    private static void validatePrice(BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleViolationException("Product price must be greater than zero.");
        }
    }
}
