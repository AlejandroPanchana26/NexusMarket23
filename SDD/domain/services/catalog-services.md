# Catalog Services

Creation, management and consultation of products. Package: `application.domain.services.catalog`.

General rules, the `AccessValidator` helper and the access matrix are described in `SDD/domain/Domain Services.md`.

---

## CreateProductService

**Role:** `SELLER` (active).

| Method                  | Business Rule                                                                                                  |
| ----------------------- | ------------------------------------------------------------------------------------------------------------- |
| create(User, Product)   | The product name is required. The product is linked to the seller and stored without status; it is not visible until it is published. |

**Ports:** `ProductRepositoryPort`.

## ManageProductService

**Role:** `SELLER`. A seller can only manage its own products (RG-03).

| Method                                        | Business Rule                                                      |
| --------------------------------------------- | ----------------------------------------------------------------- |
| publish(User, String)                         | The seller must be active. Applies `Product.publish`.             |
| suspend(User, String)                         | Applies `Product.suspend`.                                        |
| discontinue(User, String)                     | Applies `Product.discontinue`.                                    |
| changePrice(User, String, BigDecimal)         | Applies `Product.changePrice`.                                    |
| addVariant(User, String, String)              | Applies `Product.addVariant`.                                     |

**Ports:** `ProductRepositoryPort`.

## ConsultCatalogService

| Method             | Roles     | Business Rule                                              |
| ------------------ | --------- | --------------------------------------------------------- |
| listPublished()    | public    | Returns the public catalog: only `PUBLISHED` products.    |
| listOwn(User)      | `SELLER`  | Returns all the seller's products, published or not.      |

**Ports:** `ProductRepositoryPort`.
