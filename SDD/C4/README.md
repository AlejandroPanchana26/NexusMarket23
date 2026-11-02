# C4 Model — NexusMarket

The C4 model describes a software system at four levels of detail, like zooming in on a map: **Context → Containers → Components → Code**. This folder applies it to NexusMarket and adds one document per architectural layer and one with the main request flows.

The diagrams are written in **Mermaid**. GitHub renders them automatically as images; in other editors they appear as text.

| Document | Content |
| -------- | ------- |
| `01-level-1-context.md` | The system and the people who use it |
| `02-level-2-containers.md` | Application, MySQL and MongoDB |
| `03-level-3-components.md` | Components inside the application and how they depend on each other |
| `04-level-4-code.md` | Main classes of the purchase domain and the port/adapter pattern |
| `05-layer-adapters-rest.md` | REST controllers, DTOs, mappers and error handling |
| `06-layer-adapters-usecases.md` | Use cases that implement the input ports |
| `07-layer-adapters-persistence.md` | MySQL and MongoDB adapters and the data model |
| `08-layer-adapters-security.md` | JWT authentication, access rules and CORS |
| `09-layer-domain.md` | Models, value objects, exceptions, services, ports and life cycles |
| `10-layer-infrastructure.md` | Initial data, configuration and deployment |
| `11-endpoint-flows.md` | Sequence diagrams of the main business flows |

Related documents: `SDD/Software Architecture/Software Architecture.md` (overview) and the folders `SDD/domain` and `SDD/Adapters` (detailed specifications).
