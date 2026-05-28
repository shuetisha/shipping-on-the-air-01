package com.shippingontheair.fleet.infrastructure.persistence;

import com.shippingontheair.fleet.domain.model.Drone;
import com.shippingontheair.fleet.domain.model.DroneStatus;

final class DroneMapper {

    private DroneMapper() {}

    static DroneJpaEntity toEntity(Drone drone) {
        DroneJpaEntity entity = new DroneJpaEntity();
        entity.setId(drone.getId());
        entity.setName(drone.getName());
        entity.setMaxPayloadKg(drone.getMaxPayloadKg());
        entity.setStatus(drone.getStatus().name());
        entity.setLatitude(drone.getLatitude());
        entity.setLongitude(drone.getLongitude());
        entity.setCreatedAt(drone.getCreatedAt());
        entity.setUpdatedAt(drone.getUpdatedAt());
        return entity;
    }

    static Drone toDomain(DroneJpaEntity entity) {
        return Drone.restore(
                entity.getId(),
                entity.getName(),
                entity.getMaxPayloadKg(),
                DroneStatus.valueOf(entity.getStatus()),
                entity.getLatitude(),
                entity.getLongitude(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
