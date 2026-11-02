# Level 3 — Components

```mermaid
flowchart TB
    subgraph rest["adapters.rest"]
        controllers["6 REST controllers"]
        handler["GlobalExceptionHandler"]
        restMappers["8 REST mappers + DTOs"]
    end
    subgraph security["adapters.security"]
        filter["JwtAuthenticationFilter"]
        jwt["JwtService"]
        config["SecurityConfig (rules + CORS)"]
        current["CurrentUserProvider"]
        bcrypt["BCryptPasswordEncoderAdapter"]
    end
    subgraph usecases["adapters.useCases"]
        uc["6 use cases"]
    end
    subgraph domain["domain"]
        inports["6 input ports"]
        services["28 domain services"]
        models["19 models + 14 value objects"]
        outports["13 output ports"]
    end
    subgraph persistence["adapters.persistence"]
        jpa["11 JPA adapters"]
        mongoAdapter["MongoDB adapter"]
        catalog["CatalogMapper"]
    end
    subgraph infrastructure["infrastructure"]
        seed["DatabaseSeeder"]
    end
    filter --> jwt
    controllers --> current
    controllers --> inports
    controllers --> restMappers
    uc -. implements .-> inports
    uc --> services
    services --> models
    services --> outports
    jpa -. implements .-> outports
    mongoAdapter -. implements .-> outports
    bcrypt -. implements .-> outports
    seed --> services
```

| Component | Package | Responsibility |
| --------- | ------- | -------------- |
| REST controllers | `adapters.rest.controllers` | Receive HTTP requests and call one input port each |
| Use cases | `adapters.useCases` | Implement the input ports by delegating to the services |
| Domain services | `domain.services` | Execute the business operations |
| Models and value objects | `domain.models`, `domain.valueobjects` | Hold the data and protect their own rules |
| Output ports | `domain.ports.out` | Describe what the domain needs from outside |
| Persistence adapters | `adapters.persistence` | Implement the output ports with MySQL and MongoDB |
| Security | `adapters.security` | JWT, access rules, CORS and password encoding |
| Seeder | `infrastructure.seed` | Creates the initial data through the domain services |

**Dependency rule:** every arrow points towards the domain. The domain does not know the adapters.
