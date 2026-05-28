package com.shippingontheair.fleet.domain.repository;

import com.shippingontheair.fleet.domain.model.Drone;
import com.shippingontheair.fleet.domain.model.DroneStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DroneRepository {

    Drone save(Drone drone);

    Optional<Drone> findById(UUID id);

    List<Drone> findByStatus(DroneStatus status);
}
