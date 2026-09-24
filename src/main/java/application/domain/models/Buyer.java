package application.domain.models;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.SystemRole;
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

    public void register() {
        validateIdentity();
        requireText(principalAddress, "Principal address");
        setRole(SystemRole.BUYER);
        this.commercialStatus = BuyerStatus.ACTIVE;
    }

    public boolean canPlaceOrders() {
        return BuyerStatus.ACTIVE.equals(commercialStatus);
    }

    public void addAdditionalAddress(String address) {
        requireText(address, "Address");
        if (address.equalsIgnoreCase(principalAddress) || additionalAddresses.contains(address)) {
            throw new BusinessRuleViolationException("Address is already registered for this buyer.");
        }
        additionalAddresses.add(address);
    }

    public void suspend() {
        requireStatus(BuyerStatus.ACTIVE, BuyerStatus.SUSPENDED);
        this.commercialStatus = BuyerStatus.SUSPENDED;
    }

    public void activate() {
        requireStatus(BuyerStatus.SUSPENDED, BuyerStatus.ACTIVE);
        this.commercialStatus = BuyerStatus.ACTIVE;
    }

    // El bloqueo es permanente: desde BLOCKED no existe transición de salida.
    public void block() {
        if (BuyerStatus.BLOCKED.equals(commercialStatus) || commercialStatus == null) {
            throw new InvalidStatusTransitionException("Buyer", commercialStatus, BuyerStatus.BLOCKED);
        }
        this.commercialStatus = BuyerStatus.BLOCKED;
    }

    private void requireStatus(BuyerStatus expected, BuyerStatus target) {
        if (!expected.equals(commercialStatus)) {
            throw new InvalidStatusTransitionException("Buyer", commercialStatus, target);
        }
    }
}
