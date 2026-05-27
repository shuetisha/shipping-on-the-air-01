# Scope and Architectural Choices

This document states **what v1 delivers**, **what we deliberately excluded**, and **why**, aligned with the assignment objective: analysis, design, and a prototype focused on **core functionalities** and **software architecture** (DDD + microservices).

## Assignment alignment

| Assignment expectation | How v1 addresses it |
|------------------------|---------------------|
| Domain-Driven Design from analysis to development | Bounded contexts, aggregates, ubiquitous language, layered packages per service |
| Microservices architectural style | Three deployable services, database per service, REST integration |
| Architectural effort (requirements → development) | Living docs (FR/NFR, C4, context map, ADRs) + runnable prototype |
| Core/strategic features only | Shipment creation, dispatch, tracking, completion — no UI gateway or payments |
| Deliverable on git platform | Monorepo with docs, contracts, services, demo script |

## What v1 does

1. **Shipment context** — user registers a package move (origin, destination, weight, ASAP or scheduled time).
2. **Fleet context** — drones are registered; availability and payload capacity govern assignment.
3. **Delivery context** — executes the route, simulates movement, exposes tracking (position + ETA).
4. **Dispatch** — selects a suitable drone and coordinates shipment + fleet (application orchestration in delivery-service).

## Architectural choices for v1

| Topic | Choice | Rationale |
|-------|--------|-----------|
| Number of services | 3 (shipment, fleet, delivery) | One service per core bounded context; enough to demonstrate microservices without operational overload |
| Dispatch placement | Application layer inside `delivery-service` | Orchestration is not a rich domain; keeps deployable count low while context remains explicit in docs and code |
| Integration style | Synchronous REST | Transparent for demo and examination; matches prototype scope |
| Client access | Direct HTTP (curl, Swagger) | No API gateway — not required for proving domain boundaries |
| Persistence | PostgreSQL per service (Docker) / H2 (local dev) | Clear data ownership per service |
| Tracking | JSON coordinates + ETA; scheduled simulation | Satisfies FR-03 without a map UI |
| Cross-service coupling | IDs + REST DTOs only; ports in delivery-service | Boundaries stay explicit; no shared domain JAR |

## Traceability

| Requirement | Primary artifact |
|-------------|------------------|
| FR-01 … FR-04 | [`functional-requirements.md`](../analysis/functional-requirements.md) |
| NFR-01 … NFR-06 | [`non-functional-requirements.md`](../analysis/non-functional-requirements.md) |
| Context boundaries | [`context-map.md`](context-map.md) |
| Deployment view | [`c4-containers.md`](c4-containers.md) |
