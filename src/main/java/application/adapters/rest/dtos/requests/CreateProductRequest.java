package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

// type es PHYSICAL o DIGITAL. weight se usa en físicos y downloadUrl en digitales.
public record CreateProductRequest(
        @NotBlank String type,
        @NotBlank String name,
        String description,
        @NotNull @Positive BigDecimal price,
        BigDecimal weight,
        String downloadUrl,
        List<String> variants) {
}
