package application.adapters.rest.dtos.responses;

import java.math.BigDecimal;
import java.util.List;

// weight solo viene en productos físicos y downloadUrl solo en digitales.
public record ProductResponse(
        String identifier,
        String type,
        String name,
        String description,
        BigDecimal price,
        String status,
        List<String> variants,
        String sellerIdentification,
        String sellerName,
        BigDecimal weight,
        String downloadUrl) {
}
