# Layer — Persistence Adapters

## Data Model (MySQL)

Relations are stored as identifiers; the adapters rebuild the related domain objects.

```mermaid
erDiagram
    persons ||--o{ buyer_addresses : has
    persons ||--o| users : "has account"
    persons ||--o{ warehouses : owns
    persons ||--o{ products : sells
    products ||--o{ product_variants : has
    products ||--o{ inventories : "stocked as"
    warehouses ||--o{ inventories : stores
    persons ||--o| shopping_carts : has
    shopping_carts ||--o{ cart_items : contains
    persons ||--o{ orders : places
    orders ||--o{ order_details : contains
    warehouses ||--o{ orders : fulfills
    orders ||--o| invoices : "billed by"
    orders ||--o| shipments : "shipped by"
    orders ||--o{ return_requests : "returned by"
    return_requests ||--o| refunds : "refunded by"
```

15 tables: 11 for the entities and 4 auxiliary tables for lists (`buyer_addresses`, `product_variants`, `cart_items`, `order_details`).

## Document Model (MongoDB)

Collection `inventory_movements`: one document per movement (`ENTRY`, `RESERVATION`, `SALE_EXIT`, `ADJUSTMENT`, `RETURN`) with inventory, product, warehouse, quantity, date and user.

## Adapter Pattern

```mermaid
flowchart LR
    service["Domain service"] --> port["Output port"]
    adapter["JPA adapter (@Repository)"] -. implements .-> port
    adapter --> mapper["Mapper<br>toEntity / toDomain"]
    adapter --> repo["Spring Data repository"]
    repo --> db[("MySQL")]
```

Details: `SDD/Adapters/Persistence-adapters.md`.
