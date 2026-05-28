package com.shippingontheair.fleet.application;

import com.shippingontheair.fleet.application.dto.DroneResponse;
import com.shippingontheair.fleet.application.dto.RegisterDroneRequest;
import com.shippingontheair.fleet.application.dto.ReserveDroneRequest;
import com.shippingontheair.fleet.domain.model.Drone;
import com.shippingontheair.fleet.domain.model.DroneStatus;
import com.shippingontheair.fleet.domain.repository.DroneRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DroneApplicationService {

    private final DroneRepository droneRepository;

    public DroneApplicationService(DroneRepository droneRepository) {
        this.droneRepository = droneRepository;
    }

    @Transactional
    public DroneResponse register(RegisterDroneRequest request) {
        Drone drone = Drone.register(
                request.name(),
                request.maxPayloadKg(),
                request.latitude(),
                request.longitude());
        return DroneResponse.from(droneRepository.save(drone));
    }

    @Transactional(readOnly = true)
    public List<DroneResponse> listAvailable() {
        return droneRepository.findByStatus(DroneStatus.AVAILABLE).stream()
                .map(DroneResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public DroneResponse get(UUID id) {
        return droneRepository.findById(id)
                .map(DroneResponse::from)
                .orElseThrow(() -> new DroneNotFoundException(id));
    }

    @Transactional
    public DroneResponse reserve(UUID id, ReserveDroneRequest request) {
        Drone drone = droneRepository.findById(id)
                .orElseThrow(() -> new DroneNotFoundException(id));
        drone.reserve(request.requiredPayloadKg());
        return DroneResponse.from(droneRepository.save(drone));
    }

    @Transactional
    public DroneResponse release(UUID id) {
        Drone drone = droneRepository.findById(id)
                .orElseThrow(() -> new DroneNotFoundException(id));
        drone.release();
        return DroneResponse.from(droneRepository.save(drone));
    }
}
