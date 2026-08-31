package application.domain.models;

import application.domain.valueobjects.BuyerStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Buyer extends Person {

    // Dirección habitual de entrega del comprador.
    private String principalAddress;

    // Direcciones secundarias de entrega. Se inicializa vacía porque son opcionales.
    private List<String> additionalAddresses = new ArrayList<>();

    private BuyerStatus commercialStatus;
}
