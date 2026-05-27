# C4 — Containers

Deployment view of the prototype.

```mermaid
flowchart TB
  client[curl_or_Swagger]
  shipment[shipment_service:8081]
  fleet[fleet_service:8082]
  delivery[delivery_service:8083]
  db1[(shipment_db)]
  db2[(fleet_db)]
  db3[(delivery_db)]

  client --> shipment
  client --> fleet
  client --> delivery
  delivery -->|REST| shipment
  delivery -->|REST| fleet
  shipment --> db1
  fleet --> db2
  delivery --> db3
```

## Responsibilities

| Container | Technology | Data |
|-----------|------------|------|
| shipment-service | Spring Boot, JPA, Flyway | Own database (`shipment`) |
| fleet-service | Spring Boot, JPA, Flyway | Own database (`fleet`) |
| delivery-service | Spring Boot, JPA, RestClient, `@Scheduled` telemetry | Own database (`delivery`) |

## Dispatch

`DispatchOrchestrator` runs in **delivery-service** (application layer). It calls shipment-service and fleet-service over HTTP to reserve a drone and update shipment status.