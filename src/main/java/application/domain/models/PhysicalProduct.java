package application.domain.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class PhysicalProduct extends Product {

    // Peso del producto, diferenciamos producto fisico de digital.
    private BigDecimal weight;
}
