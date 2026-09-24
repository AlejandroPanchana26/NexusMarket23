package application.domain.models;

import application.domain.enums.ProductType;
import application.domain.exceptions.BusinessRuleViolationException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DigitalProduct extends Product {

    // Enlace de descarga. Los productos digitales se entregan de inmediato tras el pago.
    private String downloadUrl;

    public DigitalProduct() {
        setType(ProductType.DIGITAL);
    }

    @Override
    public boolean requiresInventory() {
        return false;
    }

    @Override
    public void publish() {
        if (downloadUrl == null || downloadUrl.isBlank()) {
            throw new BusinessRuleViolationException("Digital product download URL is required.");
        }
        super.publish();
    }
}
