package com.shippingontheair.fleet.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class DroneTest {

    @Test
    void register_createsAvailableDrone() {
        Drone drone = Drone.register("Alpha", 5.0, 41.0, 28.0);

        assertThat(drone.getName()).isEqualTo("Alpha");
        assertThat(drone.getMaxPayloadKg()).isEqualTo(5.0);
        assertThat(drone.getStatus()).isEqualTo(DroneStatus.AVAILABLE);
        assertThat(drone.getLatitude()).isEqualTo(41.0);
        assertThat(drone.getLongitude()).isEqualTo(28.0);
        assertThat(drone.getId()).isNotNull();
        assertThat(drone.getCreatedAt()).isNotNull();
    }

    @Test
    void register_rejectsBlankName() {
        assertThatThrownBy(() -> Drone.register("", 5.0, 0, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name is required");
    }

    @Test
    void register_rejectsNullName() {
        assertThatThrownBy(() -> Drone.register(null, 5.0, 0, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name is required");
    }

    @Test
    void register_rejectsZeroPayload() {
        assertThatThrownBy(() -> Drone.register("Alpha", 0, 0, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("maxPayloadKg must be positive");
    }

    @Test
    void register_rejectsNegativePayload() {
        assertThatThrownBy(() -> Drone.register("Alpha", -1.0, 0, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("maxPayloadKg must be positive");
    }

    @Test
    void reserve_transitionsToDroneInMission() {
        Drone drone = Drone.register("Alpha", 5.0, 0, 0);
        drone.reserve(3.0);
        assertThat(drone.getStatus()).isEqualTo(DroneStatus.IN_MISSION);
    }

    @Test
    void reserve_failsWhenNotAvailable() {
        Drone drone = Drone.register("Alpha", 5.0, 0, 0);
        drone.reserve(3.0);

        assertThatThrownBy(() -> drone.reserve(1.0))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("drone is not available");
    }

    @Test
    void reserve_failsWhenPayloadExceedsCapacity() {
        Drone drone = Drone.register("Alpha", 5.0, 0, 0);

        assertThatThrownBy(() -> drone.reserve(6.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("payload exceeds drone capacity");
    }

    @Test
    void reserve_succeedsAtExactCapacity() {
        Drone drone = Drone.register("Alpha", 5.0, 0, 0);
        drone.reserve(5.0);
        assertThat(drone.getStatus()).isEqualTo(DroneStatus.IN_MISSION);
    }

    @Test
    void release_transitionsBackToAvailable() {
        Drone drone = Drone.register("Alpha", 5.0, 0, 0);
        drone.reserve(3.0);
        drone.release();
        assertThat(drone.getStatus()).isEqualTo(DroneStatus.AVAILABLE);
    }

    @Test
    void release_failsWhenNotInMission() {
        Drone drone = Drone.register("Alpha", 5.0, 0, 0);

        assertThatThrownBy(() -> drone.release())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("drone is not in mission");
    }

    @Test
    void restore_rebuildsExistingDrone() {
        Drone original = Drone.register("Alpha", 5.0, 41.0, 28.0);
        Drone restored = Drone.restore(
                original.getId(),
                original.getName(),
                original.getMaxPayloadKg(),
                original.getStatus(),
                original.getLatitude(),
                original.getLongitude(),
                original.getCreatedAt(),
                original.getUpdatedAt());

        assertThat(restored.getId()).isEqualTo(original.getId());
        assertThat(restored.getName()).isEqualTo(original.getName());
        assertThat(restored.getStatus()).isEqualTo(original.getStatus());
    }
}
