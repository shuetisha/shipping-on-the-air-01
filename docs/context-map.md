# Context Map

Bounded contexts and relationships implemented in the prototype.

```mermaid
flowchart TB
  subgraph shipmentCtx [ShipmentContext]
    SH[Shipment]
  end
  subgraph fleetCtx [FleetContext]
    FL[Drone]
  end
  subgraph deliveryCtx [DeliveryContext]
    DL[Delivery]
  end
  subgraph dispatchCtx [DispatchContext]
    DS[DispatchOrchestration]
  end

  DS -->|Customer-Supplier REST| SH
  DS -->|Customer-Supplier REST| FL
  DL -->|uses| DS
  DL -->|updates on complete| SH
  DL -->|releases| FL
```

## Relationships

| Upstream | Downstream | Pattern | Integration |
|----------|------------|---------|-------------|
| Shipment | Dispatch | Customer-Supplier | REST: read shipment, patch status |
| Fleet | Dispatch | Open Host Service | REST: list available, reserve, release |
| Delivery | Shipment | Customer-Supplier | REST: mark delivered / failed |
| Delivery | Fleet | Customer-Supplier | REST: release drone |

## Deployment mapping

| Context | Service |
|---------|---------|
| Shipment | shipment-service |
| Fleet | fleet-service |
| Delivery + Dispatch (application) | delivery-service |

Dispatch is a **conceptual context** implemented as application logic in delivery-service (`DispatchOrchestrator`), documented here for DDD clarity.
