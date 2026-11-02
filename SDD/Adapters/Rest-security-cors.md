# REST Security and CORS

## 1. Purpose

The REST API is protected with JSON Web Tokens (JWT) and Spring Security. A user logs in once with username and password, receives a signed token, and sends that token in every request.

Security works in **two layers**:

| Layer | Where | What It Checks |
| ----- | ----- | -------------- |
| 1. By address | `SecurityConfig` | Each group of endpoints can only be used by its role. |
| 2. By business rule | Domain services (`AccessValidator`) | The user is active, has an allowed role, and only acts on their own information (RG-01, RG-03). |

If one layer failed, the other would still protect the system.

---

## 2. Components

All components are in `application.adapters.security`.

| Component | Responsibility |
| --------- | -------------- |
| `JwtService` | Creates tokens (`generateToken`) and verifies them (`getUsername`). The secret key and the lifetime come from `application.properties` (`jwt.secret`, `jwt.expiration-minutes`). |
| `JwtAuthenticationFilter` | Runs once per request, before the controllers. Reads the `Authorization: Bearer <token>` header, verifies the token, loads the user again from the database and, if the user is active, registers them in Spring Security with the authority `ROLE_<role>`. |
| `SecurityConfig` | Defines the access rules by address and the CORS policy, disables sessions (stateless) and adds the JWT filter. |
| `CurrentUserProvider` | Gives the controllers the authenticated `User`, taken from the Spring Security context. |
| `BCryptPasswordEncoderAdapter` | Implements `PasswordEncoderPort` with BCrypt. Passwords are never stored as plain text. |

Library: JJWT 0.12.6 (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`), signing algorithm HS256.

---

## 3. Token Content

A token has three parts separated by dots: header, payload and signature.

| Claim | Value |
| ----- | ----- |
| `sub` | Username |
| `role` | Role code (for example `BUYER`) |
| `iat` | Issue date |
| `exp` | Expiration date (60 minutes by default) |

The payload can be read by anyone, because it is only encoded. What makes the token secure is the **signature**: changing any character, or signing with another key, makes the verification fail. For that reason the token never contains the password.

---

## 4. Request Flow

```text
1. POST /api/public/login  {username, password}
      → LoginService checks the password with BCrypt
      → JwtService.generateToken(user)
      ← {token, tokenType: "Bearer", expiresInMinutes: 60, user}

2. GET /api/buyer/cart  with header  Authorization: Bearer <token>
      → JwtAuthenticationFilter: valid signature? not expired? user exists and is active?
      → SecurityConfig: does the role match /api/buyer/**?
      → BuyerRestController → CurrentUserProvider.getCurrentUser()
      → BuyerPort → services (second layer of rules)
```

---

## 5. Access Rules

| Address | Access |
| ------- | ------ |
| `/api/public/**` | Everyone (register, login, public catalog) |
| `/actuator/health` | Everyone |
| `/api/buyer/**` | `BUYER` |
| `/api/seller/**` | `SELLER` |
| `/api/operator/**` | `LOGISTICS_OPERATOR` |
| `/api/admin/**` | `ADMINISTRATOR` |
| `/api/supervisor/**` | `SUPERVISOR` |
| Any other address | Any authenticated user |

| Situation | Response |
| --------- | -------- |
| No token, malformed token, wrong signature or expired token | `401 Unauthorized` |
| Valid token, but the role does not match the address | `403 Forbidden` |
| Valid token and role, but the service rejects the operation (for example, a seller changing another seller's product) | `403 Forbidden` (from `GlobalExceptionHandler`) |

---

## 6. Design Decisions

* **Stateless.** The server keeps no sessions (`SessionCreationPolicy.STATELESS`). Each request is identified only by its token.
* **Blocked users lose access immediately.** The filter loads the user from the database in every request and checks that the user is active. Blocking a user invalidates their token at once, even before it expires.
* **CSRF disabled.** CSRF protection is meant for browser forms with sessions; a stateless REST API that uses tokens does not need it.
* **No logout with token invalidation.** The functional specification does not require it; a token stops working when it expires or when the user is blocked or deactivated.
* **Configurable secret.** The secret must have at least 32 characters. The default value is for local development only and can be replaced with the `JWT_SECRET` environment variable.

---

## 7. CORS

Browsers only allow a web page to call an API of the same origin (address and port). A frontend running on `http://localhost:3000` and the API on `http://localhost:8080` are different origins, so the browser blocks the call unless the API allows that origin. Tools such as Postman do not apply this rule.

`SecurityConfig` enables CORS with `http.cors(Customizer.withDefaults())` and defines the policy in the bean `corsConfigurationSource`:

| Setting | Value |
| ------- | ----- |
| Allowed origins | From `cors.allowed-origins` (environment variable `CORS_ALLOWED_ORIGINS`). Default: `http://localhost:3000`, `http://localhost:4200`, `http://localhost:5173` |
| Allowed methods | `GET`, `POST`, `PUT`, `PATCH`, `DELETE`, `OPTIONS` |
| Allowed headers | `Authorization`, `Content-Type` |
| Applies to | Every address (`/**`) |

Before a request with a token, the browser sends a **preflight** request with the method `OPTIONS` to ask whether the call is allowed. That is why `OPTIONS` is in the list of methods.

### Acceptance Tests

| Request | Expected Result |
| ------- | --------------- |
| `OPTIONS /api/buyer/cart` with `Origin: http://localhost:3000` | `200`, header `Access-Control-Allow-Origin: http://localhost:3000` |
| `OPTIONS /api/buyer/cart` with an origin that is not in the list | `403 Invalid CORS request` |

```bash
curl -i -X OPTIONS http://localhost:8080/api/buyer/cart -H "Origin: http://localhost:3000" -H "Access-Control-Request-Method: GET" -H "Access-Control-Request-Headers: authorization"
```
