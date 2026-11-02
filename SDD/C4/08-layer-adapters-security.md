# Layer — Security Adapters

## Login

```mermaid
sequenceDiagram
    actor Client
    participant Ctrl as PublicRestController
    participant Login as LoginService
    participant Enc as BCryptPasswordEncoderAdapter
    participant Jwt as JwtService
    Client->>Ctrl: POST /api/public/login {username, password}
    Ctrl->>Login: login(username, password)
    Login->>Enc: matches(password, storedHash)
    Enc-->>Login: true
    Login-->>Ctrl: User (active)
    Ctrl->>Jwt: generateToken(user)
    Jwt-->>Ctrl: signed token (sub, role, iat, exp)
    Ctrl-->>Client: 200 {token, tokenType, expiresInMinutes, user}
```

## Protected Request

```mermaid
sequenceDiagram
    actor Client
    participant Cors as CORS (SecurityConfig)
    participant Filter as JwtAuthenticationFilter
    participant Rules as SecurityConfig rules
    participant Ctrl as Controller
    Client->>Cors: request + Origin (browsers only)
    Cors->>Filter: origin allowed
    Filter->>Filter: valid signature? not expired? user active?
    alt invalid or missing token
        Filter->>Rules: no identity
        Rules-->>Client: 401 Unauthorized
    else valid token
        Filter->>Rules: user with ROLE_<role>
        alt role does not match the address
            Rules-->>Client: 403 Forbidden
        else role matches
            Rules->>Ctrl: request
            Ctrl-->>Client: response
        end
    end
```

| Component | Responsibility |
| --------- | -------------- |
| `JwtService` | Creates and verifies tokens (HS256, JJWT 0.12.6) |
| `JwtAuthenticationFilter` | Identifies the user of each request |
| `SecurityConfig` | Access rules by address, CORS, stateless sessions |
| `CurrentUserProvider` | Gives the authenticated user to the controllers |
| `BCryptPasswordEncoderAdapter` | Implements `PasswordEncoderPort` |

Details: `SDD/Adapters/Rest-security-cors.md`.
