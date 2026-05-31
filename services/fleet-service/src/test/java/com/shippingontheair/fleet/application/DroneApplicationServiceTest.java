package com.shippingontheair.fleet.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shippingontheair.fleet.application.dto.DroneResponse;
import com.shippingontheair.fleet.application.dto.RegisterDroneRequest;
import com.shippingontheair.fleet.application.dto.ReserveDroneRequest;
import com.shippingontheair.fleet.domain.model.Drone;
import com.shippingontheair.fleet.domain.model.DroneStatus;
import com.shippingontheair.fleet.domain.repository.DroneRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DroneApplicationServiceTest {

    @Mock
    private DroneRepository droneRepository;

    private DroneApplicationService service;

    @BeforeEach
    void setUp() {
        service = new DroneApplicationService(droneRepository);
    }

    @Test
    void register_savesAndReturnsDrone() {
        Drone drone = Drone.register("Alpha", 5.0, 41.0, 28.0);
        when(droneRepository.save(any())).thenReturn(drone);

        DroneResponse response = service.register(new RegisterDroneRequest("Alpha", 5.0, 41.0, 28.0));

        assertThat(response.name()).isEqualTo("Alpha");
        assertThat(response.maxPayloadKg()).isEqualTo(5.0);
        assertThat(response.status()).isEqualTo(DroneStatus.AVAILABLE);
        verify(droneRepository).save(any());
    }

    @Test
    void listAvailable_returnsAvailableDrones() {
        Drone drone1 = Drone.register("Alpha", 5.0, 0, 0);
        Drone drone2 = Drone.register("Beta", 3.0, 0, 0);
        when(droneRepository.findByStatus(DroneStatus.AVAILABLE)).thenReturn(List.of(drone1, drone2));

        List<DroneResponse> result = service.listAvailable();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(DroneResponse::name).containsExactly("Alpha", "Beta");
    }

    @Test
    void listAvailable_returnsEmptyWhenNoneAvailable() {
        when(droneRepository.findByStatus(DroneStatus.AVAILABLE)).thenReturn(List.of());

        List<DroneResponse> result = service.listAvailable();

        assertThat(result).isEmpty();
    }

    @Test
    void get_returnsDroneWhenFound() {
        UUID id = UUID.randomUUID();
        Drone drone = Drone.register("Alpha", 5.0, 0, 0);
        when(droneRepository.findById(id)).thenReturn(Optional.of(drone));

        DroneResponse response = service.get(id);

        assertThat(response.name()).isEqualTo("Alpha");
    }

    @Test
    void get_throwsWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(droneRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(id))
                .isInstanceOf(DroneNotFoundException.class);
    }

    @Test
    void reserve_reservesDroneAndReturnsResponse() {
        UUID id = UUID.randomUUID();
        Drone drone = Drone.register("Alpha", 5.0, 0, 0);
        when(droneRepository.findById(id)).thenReturn(Optional.of(drone));
        when(droneRepository.save(any())).thenReturn(drone);

        DroneResponse response = service.reserve(id, new ReserveDroneRequest(3.0));

        assertThat(response.status()).isEqualTo(DroneStatus.IN_MISSION);
    }

    @Test
    void reserve_throwsWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(droneRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.reserve(id, new ReserveDroneRequest(3.0)))
                .isInstanceOf(DroneNotFoundException.class);
    }

    @Test
    void release_releasesDroneAndReturnsResponse() {
        UUID id = UUID.randomUUID();
        Drone drone = Drone.register("Alpha", 5.0, 0, 0);
        drone.reserve(3.0);
        when(droneRepository.findById(id)).thenReturn(Optional.of(drone));
        when(droneRepository.save(any())).thenReturn(drone);

        DroneResponse response = service.release(id);

        assertThat(response.status()).isEqualTo(DroneStatus.AVAILABLE);
    }

    @Test
    void release_throwsWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(droneRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.release(id))
                .isInstanceOf(DroneNotFoundException.class);
    }
}
