package com.shippingontheair.fleet.application;

import com.shippingontheair.fleet.application.dto.DroneResponse;
import com.shippingontheair.fleet.application.dto.RegisterDroneRequest;
import com.shippingontheair.fleet.application.dto.ReserveDroneRequest;
import com.shippingontheair.fleet.domain.model.Drone;
import com.shippingontheair.fleet.domain.model.DroneStatus;
import com.shippingontheair.fleet.domain.repository.DroneRepository;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DroneApplicationService {

    private static final Logger log = LoggerFactory.getLogger(DroneApplicationService.class);

    private final DroneRepository droneRepository;

    public DroneApplicationService(DroneRepository droneRepository) {
        this.droneRepository = droneRepository;
    }

    @Transactional
    public DroneResponse register(RegisterDroneRequest request) {
        log.info("Registering drone: name={}, maxPayloadKg={}, lat={}, lon={}",
                request.name(), request.maxPayloadKg(), request.latitude(), request.longitude());
        Drone drone = Drone.register(
                request.name(),
                request.maxPayloadKg(),
                request.latitude(),
                request.longitude());
        Drone saved = droneRepository.save(drone);
        log.info("Drone registered: id={}, name={}", saved.getId(), saved.getName());
        return DroneResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<DroneResponse> listAvailable() {
        List<Drone> drones = droneRepository.findByStatus(DroneStatus.AVAILABLE);
        log.debug("Available drones: count={}", drones.size());
        return drones.stream().map(DroneResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public DroneResponse get(UUID id) {
        log.debug("Fetching drone: id={}", id);
        return droneRepository.findById(id)
                .map(DroneResponse::from)
                .orElseThrow(() -> new DroneNotFoundException(id));
    }

    @Transactional
    public DroneResponse reserve(UUID id, ReserveDroneRequest request) {
        log.info("Reserving drone: id={}, requiredPayloadKg={}", id, request.requiredPayloadKg());
        Drone drone = droneRepository.findById(id)
                .orElseThrow(() -> new DroneNotFoundException(id));
        drone.reserve(request.requiredPayloadKg());
        Drone saved = droneRepository.save(drone);
        log.info("Drone reserved: id={}, status={}", saved.getId(), saved.getStatus());
        return DroneResponse.from(saved);
    }

    @Transactional
    public DroneResponse release(UUID id) {
        log.info("Releasing drone: id={}", id);
        Drone drone = droneRepository.findById(id)
                .orElseThrow(() -> new DroneNotFoundException(id));
        drone.release();
        Drone saved = droneRepository.save(drone);
        log.info("Drone released: id={}, status={}", saved.getId(), saved.getStatus());
        return DroneResponse.from(saved);
    }
}
