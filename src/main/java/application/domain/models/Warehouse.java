package application.domain.models;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.valueobjects.WarehouseType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Warehouse {

    private String identifier;
    private String name;
    private String address;

    // Tipo de bodega ya sea del marketplace o de un vendedor.
    private WarehouseType type;

    // Si es bodega de un vendedor, aquí va ese vendedor, si es del marketplace, queda vacío
    private Seller owner;

    public void register() {
        if (name == null || name.isBlank() || address == null || address.isBlank()) {
            throw new BusinessRuleViolationException("Warehouse name and address are required.");
        }
        if (type == null) {
            throw new BusinessRuleViolationException("Warehouse type is required.");
        }
        if (isMarketplaceWarehouse() && owner != null) {
            throw new BusinessRuleViolationException("A marketplace warehouse cannot have a seller owner.");
        }
        if (!isMarketplaceWarehouse() && owner == null) {
            throw new BusinessRuleViolationException("A seller warehouse must have an owner.");
        }
    }

    public void assignTo(Seller seller) {
        if (seller == null) {
            throw new BusinessRuleViolationException("Seller is required.");
        }
        this.type = WarehouseType.SELLER;
        this.owner = seller;
    }

    public boolean isMarketplaceWarehouse() {
        return WarehouseType.MARKETPLACE.equals(type);
    }

    public boolean belongsTo(Seller seller) {
        return owner != null && seller != null
                && owner.getIdentification() != null
                && owner.getIdentification().equals(seller.getIdentification());
    }
}
