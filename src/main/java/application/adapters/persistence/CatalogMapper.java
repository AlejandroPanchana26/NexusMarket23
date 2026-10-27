package application.adapters.persistence;

import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.DomainCatalog;
import application.domain.valueobjects.InventoryMovementType;
import application.domain.valueobjects.InventoryStatus;
import application.domain.valueobjects.InvoiceStatus;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.RefundStatus;
import application.domain.valueobjects.ReturnStatus;
import application.domain.valueobjects.SellerStatus;
import application.domain.valueobjects.ShipmentStatus;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.WarehouseType;

// Convierte los catálogos del dominio en texto para guardarlos, y el texto guardado de vuelta en catálogo.
public final class CatalogMapper {

    private CatalogMapper() {
    }

    // Del catálogo al texto que se guarda en la base de datos (su code).
    public static String toCode(DomainCatalog value) {
        return value == null ? null : value.getCode();
    }

    public static SystemRole systemRole(String code) {
        return find(code, new SystemRole[] {SystemRole.BUYER, SystemRole.SELLER, SystemRole.LOGISTICS_OPERATOR,
                SystemRole.ADMINISTRATOR, SystemRole.SUPERVISOR});
    }

    public static UserStatus userStatus(String code) {
        return find(code, new UserStatus[] {UserStatus.ACTIVE, UserStatus.INACTIVE, UserStatus.BLOCKED});
    }

    public static BuyerStatus buyerStatus(String code) {
        return find(code, new BuyerStatus[] {BuyerStatus.ACTIVE, BuyerStatus.SUSPENDED, BuyerStatus.BLOCKED});
    }

    public static SellerStatus sellerStatus(String code) {
        return find(code, new SellerStatus[] {SellerStatus.ACTIVE, SellerStatus.SUSPENDED, SellerStatus.INACTIVE});
    }

    public static WarehouseType warehouseType(String code) {
        return find(code, new WarehouseType[] {WarehouseType.MARKETPLACE, WarehouseType.SELLER});
    }

    public static ProductStatus productStatus(String code) {
        return find(code, new ProductStatus[] {ProductStatus.PUBLISHED, ProductStatus.SUSPENDED, ProductStatus.DISCONTINUED});
    }

    public static InventoryStatus inventoryStatus(String code) {
        return find(code, new InventoryStatus[] {InventoryStatus.AVAILABLE, InventoryStatus.RESERVED, InventoryStatus.DAMAGED});
    }

    public static InventoryMovementType inventoryMovementType(String code) {
        return find(code, new InventoryMovementType[] {InventoryMovementType.ENTRY, InventoryMovementType.RESERVATION,
                InventoryMovementType.SALE_EXIT, InventoryMovementType.ADJUSTMENT, InventoryMovementType.RETURN});
    }

    public static OrderStatus orderStatus(String code) {
        return find(code, new OrderStatus[] {OrderStatus.CART, OrderStatus.PENDING_PAYMENT, OrderStatus.PAID,
                OrderStatus.SHIPPED, OrderStatus.DELIVERED});
    }

    public static InvoiceStatus invoiceStatus(String code) {
        return find(code, new InvoiceStatus[] {InvoiceStatus.ISSUED, InvoiceStatus.PAID, InvoiceStatus.CANCELLED});
    }

    public static ShipmentStatus shipmentStatus(String code) {
        return find(code, new ShipmentStatus[] {ShipmentStatus.PREPARING, ShipmentStatus.SHIPPED,
                ShipmentStatus.IN_TRANSIT, ShipmentStatus.DELIVERED});
    }

    public static ReturnStatus returnStatus(String code) {
        return find(code, new ReturnStatus[] {ReturnStatus.REQUESTED, ReturnStatus.APPROVED,
                ReturnStatus.REJECTED, ReturnStatus.COMPLETED});
    }

    public static RefundStatus refundStatus(String code) {
        return find(code, new RefundStatus[] {RefundStatus.PENDING, RefundStatus.PROCESSED, RefundStatus.REJECTED});
    }

    // Busca entre los valores posibles el que tenga ese code. T representa cualquier catálogo.
    private static <T extends DomainCatalog> T find(String code, T[] values) {
        for (T value : values) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
