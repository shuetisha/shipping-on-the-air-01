package com.shippingontheair.shipment.infrastructure.persistence;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentJpaRepository extends JpaRepository<ShipmentJpaEntity, UUID> {}
