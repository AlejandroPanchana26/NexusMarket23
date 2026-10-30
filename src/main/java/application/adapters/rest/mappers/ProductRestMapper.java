package application.adapters.rest.mappers;

import application.adapters.persistence.CatalogMapper;
import application.adapters.rest.dtos.requests.CreateProductRequest;
import application.adapters.rest.dtos.responses.ProductResponse;
import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.models.DigitalProduct;
import application.domain.models.PhysicalProduct;
import application.domain.models.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class ProductRestMapper {

    private ProductRestMapper() {
    }

    // Versión completa, para el vendedor dueño del producto.
    public static ProductResponse toResponse(Product product) {
        return build(product, true);
    }

    // Versión del catálogo público: nunca muestra el enlace de descarga,
    // porque un producto digital solo se entrega después del pago.
    public static ProductResponse toPublicResponse(Product product) {
        return build(product, false);
    }

    private static ProductResponse build(Product product, boolean includeDownloadUrl) {
        BigDecimal weight = null;
        String downloadUrl = null;
        if (product instanceof PhysicalProduct physical) {
            weight = physical.getWeight();
        } else if (product instanceof DigitalProduct digital && includeDownloadUrl) {
            downloadUrl = digital.getDownloadUrl();
        }
        return new ProductResponse(
                product.getIdentifier(),
                product.getType().name(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                CatalogMapper.toCode(product.getStatus()),
                new ArrayList<>(product.getVariants()),
                product.getSeller() == null ? null : product.getSeller().getIdentification(),
                product.getSeller() == null ? null : product.getSeller().getName(),
                weight,
                downloadUrl);
    }

    public static List<ProductResponse> toResponseList(List<Product> products) {
        List<ProductResponse> responses = new ArrayList<>();
        for (Product product : products) {
            responses.add(toResponse(product));
        }
        return responses;
    }

    public static List<ProductResponse> toPublicResponseList(List<Product> products) {
        List<ProductResponse> responses = new ArrayList<>();
        for (Product product : products) {
            responses.add(toPublicResponse(product));
        }
        return responses;
    }

    // Según el tipo indicado se crea un producto físico o uno digital.
    public static Product toProduct(CreateProductRequest request) {
        Product product;
        if ("PHYSICAL".equals(request.type())) {
            PhysicalProduct physical = new PhysicalProduct();
            physical.setWeight(request.weight());
            product = physical;
        } else if ("DIGITAL".equals(request.type())) {
            DigitalProduct digital = new DigitalProduct();
            digital.setDownloadUrl(request.downloadUrl());
            product = digital;
        } else {
            throw new BusinessRuleViolationException("Product type must be PHYSICAL or DIGITAL.");
        }
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        if (request.variants() != null) {
            for (String variant : request.variants()) {
                product.addVariant(variant);
            }
        }
        return product;
    }
}
