## Context

Delivery and dispatch need read/update access to shipment and fleet data. The prototype must be easy to run, debug, and demonstrate for course evaluation.

## Decision

Use **HTTP/JSON REST** between services. Delivery-service implements **ports** (`ShipmentPort`, `FleetPort`) with **RestClient** adapters in `infrastructure/client/`.

## Consequences

- End-to-end flow is visible in logs and a single demo script.
- Services are temporarily coupled at runtime (acceptable for this prototype scope).
- Compensation on dispatch failure: release drone, mark shipment failed.
