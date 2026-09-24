package application.domain.models;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.valueobjects.InventoryStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Inventory {

    private String identifier;

    // El inventario aplica solo a productos físicos; los digitales no manejan existencias.
    private PhysicalProduct product;

    // Bodega donde se encuentran físicamente las existencias.
    private Warehouse warehouse;

    // Cantidad disponible para venta.
    private Integer availableQuantity;

    // Cantidad apartada para pedidos en proceso.
    private Integer reservedQuantity;

    private InventoryStatus status;

    public void register() {
        if (product == null || warehouse == null) {
            throw new BusinessRuleViolationException("Inventory must be linked to a product and a warehouse.");
        }
        this.availableQuantity = 0;
        this.reservedQuantity = 0;
        this.status = InventoryStatus.AVAILABLE;
    }

    public void addStock(int quantity) {
        requirePositive(quantity);
        requireNotDamaged();
        this.availableQuantity += quantity;
        refreshStatus();
    }

    // Regla del PDF: no se reserva inventario dañado ni más de lo que hay disponible.
    public void reserve(int quantity) {
        requirePositive(quantity);
        requireNotDamaged();
        if (quantity > availableQuantity) {
            throw new BusinessRuleViolationException("Not enough available stock to reserve.");
        }
        this.availableQuantity -= quantity;
        this.reservedQuantity += quantity;
        refreshStatus();
    }

    // La salida por venta descuenta de lo que ya estaba reservado para el pedido.
    public void confirmSale(int quantity) {
        requirePositive(quantity);
        if (quantity > reservedQuantity) {
            throw new BusinessRuleViolationException("Cannot ship more units than the reserved stock.");
        }
        this.reservedQuantity -= quantity;
        refreshStatus();
    }

    public void receiveReturn(int quantity) {
        requirePositive(quantity);
        this.availableQuantity += quantity;
        refreshStatus();
    }

    public void adjust(int newAvailableQuantity) {
        if (newAvailableQuantity < 0) {
            throw new BusinessRuleViolationException("Stock cannot be negative.");
        }
        this.availableQuantity = newAvailableQuantity;
        refreshStatus();
    }

    public void markAsDamaged() {
        this.status = InventoryStatus.DAMAGED;
    }

    public boolean hasAvailable(int quantity) {
        return !isDamaged() && availableQuantity != null && availableQuantity >= quantity;
    }

    public boolean isDamaged() {
        return InventoryStatus.DAMAGED.equals(status);
    }

    private void requirePositive(int quantity) {
        if (quantity <= 0) {
            throw new BusinessRuleViolationException("Quantity must be greater than zero.");
        }
    }

    private void requireNotDamaged() {
        if (isDamaged()) {
            throw new BusinessRuleViolationException("Damaged inventory cannot be used.");
        }
    }

    // RESERVED indica que todo lo que queda está apartado para pedidos.
    private void refreshStatus() {
        if (isDamaged()) {
            return;
        }
        if (availableQuantity == 0 && reservedQuantity > 0) {
            this.status = InventoryStatus.RESERVED;
        } else {
            this.status = InventoryStatus.AVAILABLE;
        }
    }
}
