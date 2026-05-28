package com.shippingontheair.delivery.infrastructure.persistence;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryJpaRepository extends JpaRepository<DeliveryJpaEntity, UUID> {

    List<DeliveryJpaEntity> findByStatus(String status);
}
