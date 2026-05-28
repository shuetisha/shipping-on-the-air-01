package com.shippingontheair.fleet.infrastructure.persistence;

import com.shippingontheair.fleet.domain.model.Drone;
import com.shippingontheair.fleet.domain.model.DroneStatus;
import com.shippingontheair.fleet.domain.repository.DroneRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class JpaDroneRepository implements DroneRepository {

    private final DroneJpaRepository jpaRepository;

    public JpaDroneRepository(DroneJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Drone save(Drone drone) {
        return DroneMapper.toDomain(jpaRepository.save(DroneMapper.toEntity(drone)));
    }

    @Override
    public Optional<Drone> findById(UUID id) {
        return jpaRepository.findById(id).map(DroneMapper::toDomain);
    }

    @Override
    public List<Drone> findByStatus(DroneStatus status) {
        return jpaRepository.findByStatus(status.name()).stream().map(DroneMapper::toDomain).toList();
    }
}
