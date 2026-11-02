# Level 4 — Code

This level shows the main classes of the purchase domain and how a port connects the domain with an adapter. The complete list of attributes and methods is in `SDD/domain/Domain Model.md`.

## Purchase Domain

```mermaid
classDiagram
    class Person {
        <<abstract>>
        identification
        name
        email
        role
    }
    class Buyer {
        commercialStatus
        principalAddress
        canPlaceOrders()
    }
    class Seller {
        sellerStatus
        canSell()
        addProduct()
        addWarehouse()
    }
    class Product {
        <<abstract>>
        identifier
        price
        status
        publish()
        changePrice()
        requiresInventory()
    }
    class PhysicalProduct {
        weight
    }
    class DigitalProduct {
        downloadUrl
    }
    class Inventory {
        availableQuantity
        reservedQuantity
        reserve()
        confirmSale()
        receiveReturn()
    }
    class Warehouse {
        type
        belongsTo()
    }
    class ShoppingCart {
        addItem()
        calculateTotal()
    }
    class CartItem {
        quantity
        unitPrice
    }
    class Order {
        status
        total
        fulfillmentWarehouse
        fromCart()
        markAsPaid()
        markAsShipped()
        markAsDelivered()
    }
    class OrderDetail {
        quantity
        unitPrice
        subtotal
    }
    class Invoice {
        status
        issueFor()
        markAsPaid()
    }
    Person <|-- Buyer
    Person <|-- Seller
    Product <|-- PhysicalProduct
    Product <|-- DigitalProduct
    Seller "1" --> "*" Product : sells
    Seller "1" --> "*" Warehouse : owns
    Inventory --> PhysicalProduct
    Inventory --> Warehouse
    Buyer "1" --> "1" ShoppingCart
    ShoppingCart "1" *-- "*" CartItem
    Order "1" *-- "*" OrderDetail
    Order --> Buyer
    Order --> Warehouse : fulfilled from
    Invoice --> Order
```

## Port and Adapter

```mermaid
classDiagram
    class PlaceOrderService {
        placeOrder(User)
    }
    class OrderRepositoryPort {
        <<interface>>
        save(Order)
        findByIdentifier(String)
        findByBuyerIdentification(String)
        findByStatus(OrderStatus)
        findAll()
    }
    class OrderJpaAdapter
    class OrderJpaMapper {
        toEntity(Order)
        toDomain(...)
    }
    class SpringDataJpaOrderRepository {
        <<interface>>
    }
    class OrderJpaEntity
    PlaceOrderService --> OrderRepositoryPort : uses
    OrderJpaAdapter ..|> OrderRepositoryPort : implements
    OrderJpaAdapter --> OrderJpaMapper
    OrderJpaAdapter --> SpringDataJpaOrderRepository
    SpringDataJpaOrderRepository --> OrderJpaEntity
```

The service depends only on the interface. Replacing MySQL with another database would mean writing a new adapter, without changing `PlaceOrderService`.
