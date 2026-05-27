# Non-Functional Requirements

Architecture-focused non-functional requirements for the assignment prototype.

| ID | Requirement | v1 realisation |
|----|-------------|----------------|
| NFR-01 | **Microservices** — separate deployment and datastore per component | 3 Spring Boot applications; one database schema per service |
| NFR-02 | **DDD** — explicit domain layer, not anemic CRUD | `domain/`, `application/`, `infrastructure/`, `interfaces/` in each service |
| NFR-03 | **Service boundaries** — stable, documented APIs | OpenAPI in `contracts/openapi/`; no shared domain library |
| NFR-04 | **Modifiability** — contexts evolve independently | Ports (`ShipmentPort`, `FleetPort`); dispatch in dedicated package |
| NFR-05 | **Traceability** — requirements linked to design and code | FR/NFR IDs in docs, ADRs, README |
| NFR-06 | **Demonstrability** — reproducible end-to-end run | Docker Compose + `scripts/demo.sh` |

## Supporting qualities

- **Observability:** Spring Actuator `health` on each service
- **Interoperability:** JSON over HTTP between services
- **Data isolation:** Cross-context references by UUID only; no shared tables