package com.shippingontheair.shipment.infrastructure.persistence;

import com.shippingontheair.shipment.domain.model.Shipment;
import com.shippingontheair.shipment.domain.repository.ShipmentRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class JpaShipmentRepository implements ShipmentRepository {

    private final ShipmentJpaRepository jpaRepository;

    public JpaShipmentRepository(ShipmentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Shipment save(Shipment shipment) {
        return ShipmentMapper.toDomain(jpaRepository.save(ShipmentMapper.toEntity(shipment)));
    }

    @Override
    public Optional<Shipment> findById(UUID id) {
        return jpaRepository.findById(id).map(ShipmentMapper::toDomain);
    }
}
