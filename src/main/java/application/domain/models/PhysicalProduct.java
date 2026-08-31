package application.domain.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class PhysicalProduct extends Product {

    // Peso del producto. Los productos físicos requieren inventario y despacho.
    private BigDecimal weight;
}
