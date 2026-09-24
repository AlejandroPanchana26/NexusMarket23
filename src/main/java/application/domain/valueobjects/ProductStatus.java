package application.domain.valueobjects;

public final class ProductStatus extends DomainCatalog {

    public static final ProductStatus PUBLISHED = new ProductStatus(
            "PUBLISHED", "Published", "Product is visible in the public catalog and available.");
    public static final ProductStatus SUSPENDED = new ProductStatus(
            "SUSPENDED", "Suspended", "Product is temporarily not visible in the catalog.");
    public static final ProductStatus DISCONTINUED = new ProductStatus(
            "DISCONTINUED", "Discontinued", "Product has been permanently removed from the catalog.");

    private ProductStatus(String code, String name, String description) {
        super(code, name, description);
    }
}
