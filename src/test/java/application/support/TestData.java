package application.support;

import application.domain.models.Buyer;
import application.domain.models.DigitalProduct;
import application.domain.models.Inventory;
import application.domain.models.PhysicalProduct;
import application.domain.models.Seller;
import application.domain.models.Warehouse;

import java.math.BigDecimal;

// Crea objetos del dominio listos para usar en las pruebas.
public final class TestData {

    private TestData() {
    }

    public static Buyer activeBuyer(String identification, String name) {
        Buyer buyer = new Buyer();
        buyer.setIdentification(identification);
        buyer.setName(name);
        buyer.setEmail(identification + "@mail.com");
        buyer.setPrincipalAddress("Address " + identification);
        buyer.register();
        return buyer;
    }

    public static Seller activeSeller(String identification, String name) {
        Seller seller = new Seller();
        seller.setIdentification(identification);
        seller.setName(name);
        seller.setEmail(identification + "@store.com");
        seller.register();
        return seller;
    }

    public static Warehouse sellerWarehouse(String identifier, Seller seller) {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier(identifier);
        warehouse.setName("Warehouse " + identifier);
        warehouse.setAddress("Address " + identifier);
        seller.addWarehouse(warehouse);
        warehouse.register();
        return warehouse;
    }

    public static PhysicalProduct publishedPhysicalProduct(String identifier, String price, Seller seller) {
        PhysicalProduct product = new PhysicalProduct();
        product.setIdentifier(identifier);
        product.setName("Product " + identifier);
        product.setPrice(new BigDecimal(price));
        product.setWeight(BigDecimal.ONE);
        seller.addProduct(product);
        product.publish();
        return product;
    }

    public static DigitalProduct publishedDigitalProduct(String identifier, String price, Seller seller) {
        DigitalProduct product = new DigitalProduct();
        product.setIdentifier(identifier);
        product.setName("Product " + identifier);
        product.setPrice(new BigDecimal(price));
        product.setDownloadUrl("https://downloads/" + identifier);
        seller.addProduct(product);
        product.publish();
        return product;
    }

    public static Inventory inventoryWithStock(String identifier, PhysicalProduct product, Warehouse warehouse, int quantity) {
        Inventory inventory = new Inventory();
        inventory.setIdentifier(identifier);
        inventory.setProduct(product);
        inventory.setWarehouse(warehouse);
        inventory.register();
        if (quantity > 0) {
            inventory.addStock(quantity);
        }
        return inventory;
    }
}
