package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.NotBlank;

public record VariantRequest(@NotBlank String variant) {
}
