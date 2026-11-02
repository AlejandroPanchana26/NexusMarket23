# Identity Services

Registration of buyers, sellers and employees, login, and management of user, buyer and seller statuses. Package: `application.domain.services.identity`.

General rules, the `AccessValidator` helper and the access matrix are described in `SDD/domain/Domain Services.md`.

---

## UserAccountService

Shared steps used by every registration service.

| Method                                         | Business Rule                                                                                     |
| ---------------------------------------------- | ------------------------------------------------------------------------------------------------ |
| validateNewAccount(Person, String, String)     | Username and password are required. Identification, email, and username must not be registered. |
| createUser(Person, String, String)             | Encodes the password with `PasswordEncoderPort`, registers the user, and stores it.            |

**Ports:** `PersonRepositoryPort`, `UserRepositoryPort`, `PasswordEncoderPort`.

## PersonLookupService

| Method               | Business Rule                                                         |
| -------------------- | -------------------------------------------------------------------- |
| findSeller(String)   | Returns the person only if it is a `Seller`; otherwise, not found.   |
| findBuyer(String)    | Returns the person only if it is a `Buyer`; otherwise, not found.    |

**Ports:** `PersonRepositoryPort`.

## RegisterBuyerService

**Role:** public (no authenticated user required).

| Method                               | Steps                                                                                          |
| ------------------------------------ | --------------------------------------------------------------------------------------------- |
| register(Buyer, String, String)      | 1. `buyer.register()`. 2. Validate the new account. 3. Store the buyer. 4. Create the user.   |

**Ports:** `PersonRepositoryPort`.

## RegisterSellerService

**Role:** `ADMINISTRATOR`. Sellers cannot self-register.

| Method                                                   | Steps                                                                                                                         |
| -------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------- |
| register(User, Seller, Warehouse, String, String)        | 1. Validate role. 2. `seller.register()`. 3. Validate the new account. 4. Link and register the first warehouse. 5. Store seller and warehouse. 6. Create the user. |

**Ports:** `PersonRepositoryPort`, `WarehouseRepositoryPort`.

## RegisterEmployeeService

**Role:** `ADMINISTRATOR`.

| Method                                                     | Steps                                                                                        |
| ---------------------------------------------------------- | ------------------------------------------------------------------------------------------- |
| register(User, Employee, SystemRole, String, String)       | 1. Validate role. 2. `employee.register(role)`. 3. Validate the new account. 4. Store the employee. 5. Create the user. |

**Ports:** `PersonRepositoryPort`.

## LoginService

**Role:** public.

| Method                    | Business Rule                                                                                                   |
| ------------------------- | -------------------------------------------------------------------------------------------------------------- |
| login(String, String)     | Returns the user when the username exists, the password matches, and the user is active. A wrong username and a wrong password produce the same message so that existing usernames are not revealed. |

**Ports:** `UserRepositoryPort`, `PasswordEncoderPort`.

## ManageUserStatusService

**Role:** `ADMINISTRATOR`.

| Method                     | Business Rule                                 |
| -------------------------- | -------------------------------------------- |
| block(User, String)        | Blocks the user's access (`User.block`).     |
| deactivate(User, String)   | Deactivates the user (`User.deactivate`).    |
| activate(User, String)     | Reactivates the user (`User.activate`).      |

**Ports:** `UserRepositoryPort`.

## ManageBuyerStatusService

**Role:** `ADMINISTRATOR`.

| Method                   | Business Rule                              |
| ------------------------ | ----------------------------------------- |
| suspend(User, String)    | Suspends the buyer (`Buyer.suspend`).     |
| activate(User, String)   | Reactivates the buyer (`Buyer.activate`). |
| block(User, String)      | Blocks the buyer permanently (`Buyer.block`). |

**Ports:** `PersonRepositoryPort`.

## ManageSellerStatusService

**Role:** `ADMINISTRATOR`.

| Method                     | Business Rule                                 |
| -------------------------- | -------------------------------------------- |
| suspend(User, String)      | Suspends the seller (`Seller.suspend`).      |
| activate(User, String)     | Reactivates the seller (`Seller.activate`).  |
| deactivate(User, String)   | Deactivates the seller (`Seller.deactivate`).|

**Ports:** `PersonRepositoryPort`.

---

## ConsultUsersService

Objective OBJ-01: the administrator consults the users in order to manage them.

| Method           | Roles                         | Business Rule          |
| ---------------- | ----------------------------- | ---------------------- |
| listAll(User)    | `ADMINISTRATOR`, `SUPERVISOR` | Returns all the users. |

**Ports:** `UserRepositoryPort`.
