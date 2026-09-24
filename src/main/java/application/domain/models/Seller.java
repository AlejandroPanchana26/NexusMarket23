package application.domain.models;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.valueobjects.SellerStatus;
import application.domain.valueobjects.SystemRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Seller extends Person {

    private SellerStatus sellerStatus;

    // Bodegas del vendedor. Se inicializa vacía; se carga cuando se consultan.
    private List<Warehouse> warehouses = new ArrayList<>();

    // Productos publicados por el vendedor. Se inicializa vacía; se carga cuando se consultan.
    private List<Product> products = new ArrayList<>();

    public void register() {
        validateIdentity();
        setRole(SystemRole.SELLER);
        this.sellerStatus = SellerStatus.ACTIVE;
    }

    public boolean canSell() {
        return SellerStatus.ACTIVE.equals(sellerStatus);
    }

    public void addWarehouse(Warehouse warehouse) {
        if (warehouse == null) {
            throw new BusinessRuleViolationException("Warehouse is required.");
        }
        warehouse.assignTo(this);
        warehouses.add(warehouse);
    }

    public void addProduct(Product product) {
        if (product == null) {
            throw new BusinessRuleViolationException("Product is required.");
        }
        product.setSeller(this);
        products.add(product);
    }

    public void suspend() {
        if (!SellerStatus.ACTIVE.equals(sellerStatus)) {
            throw new InvalidStatusTransitionException("Seller", sellerStatus, SellerStatus.SUSPENDED);
        }
        this.sellerStatus = SellerStatus.SUSPENDED;
    }

    public void activate() {
        if (sellerStatus == null || SellerStatus.ACTIVE.equals(sellerStatus)) {
            throw new InvalidStatusTransitionException("Seller", sellerStatus, SellerStatus.ACTIVE);
        }
        this.sellerStatus = SellerStatus.ACTIVE;
    }

    public void deactivate() {
        if (sellerStatus == null || SellerStatus.INACTIVE.equals(sellerStatus)) {
            throw new InvalidStatusTransitionException("Seller", sellerStatus, SellerStatus.INACTIVE);
        }
        this.sellerStatus = SellerStatus.INACTIVE;
    }
}
