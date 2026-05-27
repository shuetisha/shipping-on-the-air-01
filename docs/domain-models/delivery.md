# Domain Model — Delivery

## Aggregate: `Delivery`

| Element | Type | Notes |
|---------|------|-------|
| id | UUID | Identity |
| shipmentId, droneId | UUID | References other contexts (ID only) |
| origin, destination, currentPosition | GeoPoint VO | |
| progressPercent | double | 0–100 |
| etaSeconds | int | Derived via `EtaCalculator` |
| status | IN_TRANSIT \| DELIVERED \| FAILED | |

## Domain service

`EtaCalculator` — haversine distance, ETA from remaining distance and speed.