package application.domain.models;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.valueobjects.InventoryStatus;
import application.support.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Reglas del inventario: nunca existencias negativas y nada de reservar inventario dañado.
class InventoryTest {

    private Inventory inventory;

    @BeforeEach
    void setUp() {
        Seller seller = TestData.activeSeller("200", "Tecno");
        Warehouse warehouse = TestData.sellerWarehouse("W1", seller);
        PhysicalProduct product = TestData.publishedPhysicalProduct("P1", "50000", seller);
        inventory = TestData.inventoryWithStock("I1", product, warehouse, 10);
    }

    @Test
    void addStockIncreasesAvailableQuantity() {
        inventory.addStock(5);

        assertEquals(15, inventory.getAvailableQuantity());
    }

    @Test
    void reserveMovesUnitsFromAvailableToReserved() {
        inventory.reserve(3);

        assertEquals(7, inventory.getAvailableQuantity());
        assertEquals(3, inventory.getReservedQuantity());
    }

    @Test
    void cannotReserveMoreThanAvailable() {
        assertThrows(BusinessRuleViolationException.class, () -> inventory.reserve(11));

        // Al fallar, las cantidades no cambian.
        assertEquals(10, inventory.getAvailableQuantity());
        assertEquals(0, inventory.getReservedQuantity());
    }

    @Test
    void cannotAdjustToNegativeStock() {
        assertThrows(BusinessRuleViolationException.class, () -> inventory.adjust(-1));
    }

    @Test
    void damagedInventoryCannotBeReserved() {
        inventory.markAsDamaged();

        assertEquals(InventoryStatus.DAMAGED, inventory.getStatus());
        assertThrows(BusinessRuleViolationException.class, () -> inventory.reserve(1));
    }

    @Test
    void saleAndReturnCompleteTheStockCycle() {
        inventory.reserve(3);
        inventory.confirmSale(3);
        assertEquals(7, inventory.getAvailableQuantity());
        assertEquals(0, inventory.getReservedQuantity());

        inventory.receiveReturn(3);
        assertEquals(10, inventory.getAvailableQuantity());
    }
}
