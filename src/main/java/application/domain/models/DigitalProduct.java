package application.domain.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DigitalProduct extends Product {

    // Enlace de descarga. Los productos digitales se entregan de inmediato tras el pago.
    private String downloadUrl;
}
