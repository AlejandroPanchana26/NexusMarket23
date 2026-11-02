package application.support;

import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.Person;
import application.domain.models.Product;
import application.domain.models.ShoppingCart;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.InventoryMovementRepositoryPort;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.InvoiceRepositoryPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.PasswordEncoderPort;
import application.domain.ports.out.PersonRepositoryPort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.ports.out.ShoppingCartRepositoryPort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.ProductStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Versiones en memoria de los puertos de salida. Permiten probar los servicios sin base de datos:
// es la ventaja de que el dominio dependa de interfaces y no de MySQL o MongoDB.
public final class InMemoryPorts {

    private InMemoryPorts() {
    }

    public static class People implements PersonRepositoryPort {
        public final List<Person> data = new ArrayList<>();

        public Person save(Person person) {
            if (!data.contains(person)) {
                data.add(person);
            }
            return person;
        }

        public Optional<Person> findByIdentification(String identification) {
            for (Person person : data) {
                if (person.getIdentification().equals(identification)) {
                    return Optional.of(person);
                }
            }
            return Optional.empty();
        }

        public boolean existsByIdentification(String identification) {
            return findByIdentification(identification).isPresent();
        }

        public boolean existsByEmail(String email) {
            for (Person person : data) {
                if (person.getEmail().equalsIgnoreCase(email)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class Users implements UserRepositoryPort {
        public final List<User> data = new ArrayList<>();

        public User save(User user) {
            if (!data.contains(user)) {
                data.add(user);
            }
            return user;
        }

        public Optional<User> findByUsername(String username) {
            for (User user : data) {
                if (user.getUsername().equals(username)) {
                    return Optional.of(user);
                }
            }
            return Optional.empty();
        }

        public boolean existsByUsername(String username) {
            return findByUsername(username).isPresent();
        }

        public List<User> findAll() {
            return data;
        }
    }

    public static class Warehouses implements WarehouseRepositoryPort {
        public final List<Warehouse> data = new ArrayList<>();

        public Warehouse save(Warehouse warehouse) {
            if (!data.contains(warehouse)) {
                data.add(warehouse);
            }
            return warehouse;
        }

        public Optional<Warehouse> findByIdentifier(String identifier) {
            for (Warehouse warehouse : data) {
                if (warehouse.getIdentifier().equals(identifier)) {
                    return Optional.of(warehouse);
                }
            }
            return Optional.empty();
        }

        public List<Warehouse> findBySellerIdentification(String sellerIdentification) {
            List<Warehouse> result = new ArrayList<>();
            for (Warehouse warehouse : data) {
                if (warehouse.getOwner() != null && warehouse.getOwner().getIdentification().equals(sellerIdentification)) {
                    result.add(warehouse);
                }
            }
            return result;
        }

        public List<Warehouse> findAll() {
            return data;
        }
    }

    public static class Products implements ProductRepositoryPort {
        public final List<Product> data = new ArrayList<>();

        public Product save(Product product) {
            if (!data.contains(product)) {
                data.add(product);
            }
            return product;
        }

        public Optional<Product> findByIdentifier(String identifier) {
            for (Product product : data) {
                if (product.getIdentifier().equals(identifier)) {
                    return Optional.of(product);
                }
            }
            return Optional.empty();
        }

        public List<Product> findByStatus(ProductStatus status) {
            List<Product> result = new ArrayList<>();
            for (Product product : data) {
                if (status.equals(product.getStatus())) {
                    result.add(product);
                }
            }
            return result;
        }

        public List<Product> findBySellerIdentification(String sellerIdentification) {
            List<Product> result = new ArrayList<>();
            for (Product product : data) {
                if (product.getSeller().getIdentification().equals(sellerIdentification)) {
                    result.add(product);
                }
            }
            return result;
        }
    }

    public static class Inventories implements InventoryRepositoryPort {
        public final List<Inventory> data = new ArrayList<>();

        public Inventory save(Inventory inventory) {
            if (!data.contains(inventory)) {
                data.add(inventory);
            }
            return inventory;
        }

        public Optional<Inventory> findByIdentifier(String identifier) {
            for (Inventory inventory : data) {
                if (inventory.getIdentifier().equals(identifier)) {
                    return Optional.of(inventory);
                }
            }
            return Optional.empty();
        }

        public List<Inventory> findByProductIdentifier(String productIdentifier) {
            List<Inventory> result = new ArrayList<>();
            for (Inventory inventory : data) {
                if (inventory.getProduct().getIdentifier().equals(productIdentifier)) {
                    result.add(inventory);
                }
            }
            return result;
        }

        public Optional<Inventory> findByProductAndWarehouse(String productIdentifier, String warehouseIdentifier) {
            for (Inventory inventory : data) {
                if (inventory.getProduct().getIdentifier().equals(productIdentifier)
                        && inventory.getWarehouse().getIdentifier().equals(warehouseIdentifier)) {
                    return Optional.of(inventory);
                }
            }
            return Optional.empty();
        }

        public List<Inventory> findAll() {
            return data;
        }
    }

    public static class Movements implements InventoryMovementRepositoryPort {
        public final List<InventoryMovement> data = new ArrayList<>();

        public InventoryMovement save(InventoryMovement movement) {
            data.add(movement);
            return movement;
        }

        public List<InventoryMovement> findByInventoryIdentifier(String inventoryIdentifier) {
            List<InventoryMovement> result = new ArrayList<>();
            for (InventoryMovement movement : data) {
                if (movement.getInventory().getIdentifier().equals(inventoryIdentifier)) {
                    result.add(movement);
                }
            }
            return result;
        }
    }

    public static class Carts implements ShoppingCartRepositoryPort {
        public final List<ShoppingCart> data = new ArrayList<>();

        public ShoppingCart save(ShoppingCart cart) {
            if (!data.contains(cart)) {
                data.add(cart);
            }
            return cart;
        }

        public Optional<ShoppingCart> findByBuyerIdentification(String buyerIdentification) {
            for (ShoppingCart cart : data) {
                if (cart.getBuyer().getIdentification().equals(buyerIdentification)) {
                    return Optional.of(cart);
                }
            }
            return Optional.empty();
        }
    }

    public static class Orders implements OrderRepositoryPort {
        public final List<Order> data = new ArrayList<>();

        public Order save(Order order) {
            if (!data.contains(order)) {
                data.add(order);
            }
            return order;
        }

        public Optional<Order> findByIdentifier(String identifier) {
            for (Order order : data) {
                if (order.getIdentifier().equals(identifier)) {
                    return Optional.of(order);
                }
            }
            return Optional.empty();
        }

        public List<Order> findByBuyerIdentification(String buyerIdentification) {
            List<Order> result = new ArrayList<>();
            for (Order order : data) {
                if (order.getBuyer().getIdentification().equals(buyerIdentification)) {
                    result.add(order);
                }
            }
            return result;
        }

        public List<Order> findByStatus(OrderStatus status) {
            List<Order> result = new ArrayList<>();
            for (Order order : data) {
                if (status.equals(order.getStatus())) {
                    result.add(order);
                }
            }
            return result;
        }

        public List<Order> findAll() {
            return data;
        }
    }

    public static class Invoices implements InvoiceRepositoryPort {
        public final List<Invoice> data = new ArrayList<>();

        public Invoice save(Invoice invoice) {
            if (!data.contains(invoice)) {
                data.add(invoice);
            }
            return invoice;
        }

        public Optional<Invoice> findByOrderIdentifier(String orderIdentifier) {
            for (Invoice invoice : data) {
                if (invoice.getOrder().getIdentifier().equals(orderIdentifier)) {
                    return Optional.of(invoice);
                }
            }
            return Optional.empty();
        }

        public List<Invoice> findAll() {
            return data;
        }
    }

    // Cifrado de prueba: no es seguro, solo sirve para comprobar que el servicio cifra y compara.
    public static class Encoder implements PasswordEncoderPort {
        public String encode(String rawPassword) {
            return "encoded:" + rawPassword;
        }

        public boolean matches(String rawPassword, String encodedPassword) {
            return encode(rawPassword).equals(encodedPassword);
        }
    }
}
