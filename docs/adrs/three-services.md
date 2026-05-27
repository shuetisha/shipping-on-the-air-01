## Context

The assignment requires **microservices** and **DDD**. We need enough separation to show architectural boundaries, without building infrastructure that distracts from domain and integration design.

## Decision

Deploy **three** services:

1. **shipment-service** — Shipment aggregate
2. **fleet-service** — Drone aggregate
3. **delivery-service** — Delivery aggregate + dispatch orchestration

Dispatch orchestration is **not** a fourth deployable; it lives in `delivery-service/application/dispatch/` as application logic behind `ShipmentPort` and `FleetPort`.

## Consequences

- Each core bounded context owns its data and deployment unit.
- Delivery-service coordinates cross-context workflows via REST.
- Dispatch remains visible in the context map and code structure for DDD documentation.
