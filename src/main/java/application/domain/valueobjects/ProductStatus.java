package application.domain.valueobjects;

public final class ProductStatus extends DomainCatalog {

    public static final ProductStatus PUBLISHED = new ProductStatus(
            "PUBLISHED", "Publicado", "El producto está visible en el catálogo público y disponible para la venta.");
    public static final ProductStatus SUSPENDED = new ProductStatus(
            "SUSPENDED", "Suspendido", "El producto no está visible temporalmente en el catálogo.");
    public static final ProductStatus DISCONTINUED = new ProductStatus(
            "DISCONTINUED", "Descontinuado", "El producto ha sido retirado de forma permanente del catálogo.");

    private ProductStatus(String code, String name, String description) {
        super(code, name, description);
    }
}
