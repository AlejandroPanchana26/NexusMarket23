package application.domain.models;

import application.domain.enums.ProductType;
import application.domain.exceptions.BusinessRuleViolationException;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PhysicalProduct extends Product {

    // Peso del producto, diferenciamos producto fisico de digital.
    private BigDecimal weight;

    public PhysicalProduct() {
        setType(ProductType.PHYSICAL);
    }

    @Override
    public boolean requiresInventory() {
        return true;
    }

    // Se valida el peso porque el producto físico se despacha.
    @Override
    public void publish() {
        if (weight == null || weight.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleViolationException("Physical product weight must be greater than zero.");
        }
        super.publish();
    }
}
