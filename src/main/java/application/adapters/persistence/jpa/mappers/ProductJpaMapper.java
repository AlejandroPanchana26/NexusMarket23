package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.CatalogMapper;
import application.adapters.persistence.jpa.entities.ProductJpaEntity;
import application.domain.enums.ProductType;
import application.domain.models.DigitalProduct;
import application.domain.models.PhysicalProduct;
import application.domain.models.Product;
import application.domain.models.Seller;

import java.util.ArrayList;

// Convierte entre los productos del dominio y la tabla products.
public final class ProductJpaMapper {

    private ProductJpaMapper() {
    }

    public static ProductJpaEntity toEntity(Product product) {
        ProductJpaEntity entity = new ProductJpaEntity();
        entity.setIdentifier(product.getIdentifier());
        entity.setProductType(product.getType().name());
        entity.setName(product.getName());
        entity.setDescription(product.getDescription());
        entity.setPrice(product.getPrice());
        entity.setSellerIdentification(product.getSeller().getIdentification());
        entity.setStatus(CatalogMapper.toCode(product.getStatus()));
        entity.setVariants(new ArrayList<>(product.getVariants()));

        // Cada tipo llena solo su columna propia.
        if (product instanceof PhysicalProduct physical) {
            entity.setWeight(physical.getWeight());
        } else if (product instanceof DigitalProduct digital) {
            entity.setDownloadUrl(digital.getDownloadUrl());
        }
        return entity;
    }

    // El vendedor ya viene convertido desde el adaptador.
    public static Product toDomain(ProductJpaEntity entity, Seller seller) {
        Product product;
        if (ProductType.PHYSICAL.name().equals(entity.getProductType())) {
            PhysicalProduct physical = new PhysicalProduct();
            physical.setWeight(entity.getWeight());
            product = physical;
        } else {
            DigitalProduct digital = new DigitalProduct();
            digital.setDownloadUrl(entity.getDownloadUrl());
            product = digital;
        }
        product.setIdentifier(entity.getIdentifier());
        product.setName(entity.getName());
        product.setDescription(entity.getDescription());
        product.setPrice(entity.getPrice());
        product.setSeller(seller);
        product.setStatus(CatalogMapper.productStatus(entity.getStatus()));
        product.setVariants(new ArrayList<>(entity.getVariants()));
        return product;
    }
}
