# Layer — Domain

| Package | Content | Count |
| ------- | ------- | ----- |
| `domain.models` | Entities with behavior | 19 |
| `domain.valueobjects` | Catalogs (statuses, roles, types), including the base class `DomainCatalog` | 14 |
| `domain.enums` | `ProductType` | 1 |
| `domain.exceptions` | `DomainException` and its 5 subclasses | 6 |
| `domain.services` | Business operations in 8 areas, plus `AccessValidator` | 28 |
| `domain.ports.in` | Input ports, one per role | 6 |
| `domain.ports.out` | Output ports | 13 |

## Life Cycles

### Order

```mermaid
stateDiagram-v2
    [*] --> PENDING_PAYMENT : placeOrder
    PENDING_PAYMENT --> PAID : pay
    PAID --> SHIPPED : dispatch (physical products)
    PAID --> DELIVERED : pay (digital products only)
    SHIPPED --> DELIVERED : confirm delivery
    DELIVERED --> [*]
```

A delivered order is finalized and cannot change.

### Shipment

```mermaid
stateDiagram-v2
    [*] --> PREPARING : prepare
    PREPARING --> SHIPPED : dispatch
    SHIPPED --> IN_TRANSIT : mark in transit
    IN_TRANSIT --> DELIVERED : confirm delivery
    DELIVERED --> [*]
```

### Product

```mermaid
stateDiagram-v2
    [*] --> Draft : create (no status)
    Draft --> PUBLISHED : publish
    PUBLISHED --> SUSPENDED : suspend
    SUSPENDED --> PUBLISHED : publish
    PUBLISHED --> DISCONTINUED : discontinue
    SUSPENDED --> DISCONTINUED : discontinue
    DISCONTINUED --> [*]
```

### Return and Refund

```mermaid
stateDiagram-v2
    [*] --> REQUESTED : buyer requests
    REQUESTED --> APPROVED : approve
    REQUESTED --> REJECTED : reject
    APPROVED --> COMPLETED : goods received (stock returns)
    REJECTED --> [*]
    COMPLETED --> [*]
```

```mermaid
stateDiagram-v2
    [*] --> PENDING : create refund
    PENDING --> PROCESSED : process
    PENDING --> REJECTED : reject
```

## Inventory Cycle

| Step | Available | Reserved | Movement |
| ---- | --------- | -------- | -------- |
| Register 10 units | 10 | 0 | `ENTRY` |
| Order of 3 units placed | 7 | 3 | `RESERVATION` |
| Shipment dispatched | 7 | 0 | `SALE_EXIT` |
| Return completed | 10 | 0 | `RETURN` |

Details: `SDD/domain/`.
