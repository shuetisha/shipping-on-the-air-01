package com.shippingontheair.fleet.infrastructure.persistence;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DroneJpaRepository extends JpaRepository<DroneJpaEntity, UUID> {

    List<DroneJpaEntity> findByStatus(String status);
}
