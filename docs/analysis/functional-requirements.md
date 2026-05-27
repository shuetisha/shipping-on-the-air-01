# Functional Requirements 

## Overview

Online system for delivering packages via drones. A user requests delivery of a weighted package between two locations, on a schedule (including immediately), and tracks progress until completion.

## Requirements

| ID | Requirement | v1 implementation |
|----|-------------|-------------------|
| FR-01 | Create a **shipment** with origin, destination, weight (kg), and schedule (`ASAP` or `SCHEDULED` with datetime) | `POST /shipments` — `Shipment` aggregate validates weight and schedule |
| FR-02 | **Dispatch** an available drone with sufficient payload capacity | `POST /deliveries` — `DispatchOrchestrator` selects drone, `reserve`, creates delivery |
| FR-03 | **Track** delivery: phase, current position (lat/lng), ETA (seconds) | `GET /deliveries/{id}/tracking` — updated by telemetry simulation |
| FR-04 | **Terminal outcome**: delivered or failed; shipment status consistent | On `DELIVERED`: shipment `DELIVERED`, drone `release`; on dispatch failure: shipment `FAILED`, compensation |

## User journey (demo)

```text
Register drones → Create shipment → Start delivery → Poll tracking → Shipment DELIVERED
```

## Traceability

| FR | Service(s) |
|----|------------|
| FR-01 | shipment-service |
| FR-02 | delivery-service (+ fleet-service, shipment-service) |
| FR-03, FR-04 | delivery-service (+ shipment-service, fleet-service on completion) |
