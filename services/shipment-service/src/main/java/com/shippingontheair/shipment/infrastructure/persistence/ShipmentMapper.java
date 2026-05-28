package com.shippingontheair.shipment.infrastructure.persistence;

import com.shippingontheair.shipment.domain.model.GeoPoint;
import com.shippingontheair.shipment.domain.model.ScheduleType;
import com.shippingontheair.shipment.domain.model.Shipment;
import com.shippingontheair.shipment.domain.model.ShipmentStatus;

final class ShipmentMapper {

    private ShipmentMapper() {}

    static ShipmentJpaEntity toEntity(Shipment shipment) {
        ShipmentJpaEntity entity = new ShipmentJpaEntity();
        entity.setId(shipment.getId());
        entity.setOriginLabel(shipment.getOrigin().label());
        entity.setOriginLatitude(shipment.getOrigin().latitude());
        entity.setOriginLongitude(shipment.getOrigin().longitude());
        entity.setDestinationLabel(shipment.getDestination().label());
        entity.setDestinationLatitude(shipment.getDestination().latitude());
        entity.setDestinationLongitude(shipment.getDestination().longitude());
        entity.setWeightKg(shipment.getWeightKg());
        entity.setScheduleType(shipment.getScheduleType().name());
        entity.setScheduledAt(shipment.getScheduledAt());
        entity.setStatus(shipment.getStatus().name());
        entity.setFailureReason(shipment.getFailureReason());
        entity.setCreatedAt(shipment.getCreatedAt());
        entity.setUpdatedAt(shipment.getUpdatedAt());
        return entity;
    }

    static Shipment toDomain(ShipmentJpaEntity entity) {
        return Shipment.restore(
                entity.getId(),
                new GeoPoint(entity.getOriginLabel(), entity.getOriginLatitude(), entity.getOriginLongitude()),
                new GeoPoint(entity.getDestinationLabel(), entity.getDestinationLatitude(), entity.getDestinationLongitude()),
                entity.getWeightKg(),
                ScheduleType.valueOf(entity.getScheduleType()),
                entity.getScheduledAt(),
                ShipmentStatus.valueOf(entity.getStatus()),
                entity.getFailureReason(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
