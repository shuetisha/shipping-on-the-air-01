package com.shippingontheair.shipment.domain.repository;

import com.shippingontheair.shipment.domain.model.Shipment;
import java.util.Optional;
import java.util.UUID;

public interface ShipmentRepository {

    Shipment save(Shipment shipment);

    Optional<Shipment> findById(UUID id);
}
