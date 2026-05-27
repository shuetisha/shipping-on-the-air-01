# Domain Model — Shipment

## Aggregate: `Shipment`

| Element | Type | Notes |
|---------|------|-------|
| id | UUID | Identity |
| origin, destination | GeoPoint VO | label + lat/lng |
| weightKg | double | Must be > 0 |
| scheduleType | ASAP \| SCHEDULED | |
| status | enum | State machine below |

## Invariants

- Weight must be positive.
- Scheduled shipments require `scheduledAt` in the future.

## State machine

```
REQUESTED --> DISPATCHED --> DELIVERED
     |            |
     +------------+----> FAILED
```
