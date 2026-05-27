# Domain Model — Fleet

## Aggregate: `Drone`

| Element | Type | Notes |
|---------|------|-------|
| id | UUID | Identity |
| name | String | Human-readable |
| maxPayloadKg | double | Capacity |
| status | AVAILABLE \| IN_MISSION \| MAINTENANCE | |
| latitude, longitude | double | Last known position |

## Behaviour

- `reserve(requiredPayloadKg)` — fails if not AVAILABLE or capacity insufficient
- `release()` — returns to AVAILABLE from IN_MISSION
