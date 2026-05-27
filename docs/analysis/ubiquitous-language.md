# Ubiquitous Language

| Term | Definition | Bounded context |
|------|------------|-----------------|
| **Shipment** | Customer request to move a package from origin to destination | Shipment |
| **Delivery** | Execution of a shipment by a specific drone, including route progress | Delivery |
| **Drone** | Autonomous vehicle with max payload and operational status | Fleet |
| **Dispatch** | Process of selecting and reserving a drone and starting delivery | Dispatch (app layer in v1) |
| **ASAP** | Schedule type: delivery should start as soon as possible | Shipment |
| **Telemetry** | Current position and progress along the route | Delivery |
| **ETA** | Estimated seconds until delivery completion | Delivery |
| **Reserve / Release** | Fleet operations marking a drone in/out of mission | Fleet |

## Status values

**Shipment:** `REQUESTED` → `DISPATCHED` → `DELIVERED` | `FAILED` | `CANCELLED`

**Drone:** `AVAILABLE` | `IN_MISSION` | `MAINTENANCE`

**Delivery:** `IN_TRANSIT` → `DELIVERED` | `FAILED`
