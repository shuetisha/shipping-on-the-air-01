package com.shippingontheair.delivery.infrastructure.persistence;

import com.shippingontheair.delivery.domain.model.Delivery;
import com.shippingontheair.delivery.domain.model.DeliveryStatus;
import com.shippingontheair.delivery.domain.model.GeoPoint;

final class DeliveryMapper {

    private DeliveryMapper() {}

    static DeliveryJpaEntity toEntity(Delivery delivery) {
        DeliveryJpaEntity entity = new DeliveryJpaEntity();
        entity.setId(delivery.getId());
        entity.setShipmentId(delivery.getShipmentId());
        entity.setDroneId(delivery.getDroneId());
        entity.setStatus(delivery.getStatus().name());
        entity.setOriginLatitude(delivery.getOrigin().latitude());
        entity.setOriginLongitude(delivery.getOrigin().longitude());
        entity.setDestinationLatitude(delivery.getDestination().latitude());
        entity.setDestinationLongitude(delivery.getDestination().longitude());
        entity.setCurrentLatitude(delivery.getCurrentPosition().latitude());
        entity.setCurrentLongitude(delivery.getCurrentPosition().longitude());
        entity.setProgressPercent(delivery.getProgressPercent());
        entity.setEtaSeconds(delivery.getEtaSeconds());
        entity.setTotalDistanceKm(delivery.getTotalDistanceKm());
        entity.setFailureReason(delivery.getFailureReason());
        entity.setCreatedAt(delivery.getCreatedAt());
        entity.setUpdatedAt(delivery.getUpdatedAt());
        return entity;
    }

    static Delivery toDomain(DeliveryJpaEntity entity) {
        return Delivery.restore(
                entity.getId(),
                entity.getShipmentId(),
                entity.getDroneId(),
                DeliveryStatus.valueOf(entity.getStatus()),
                new GeoPoint(entity.getOriginLatitude(), entity.getOriginLongitude()),
                new GeoPoint(entity.getDestinationLatitude(), entity.getDestinationLongitude()),
                new GeoPoint(entity.getCurrentLatitude(), entity.getCurrentLongitude()),
                entity.getProgressPercent(),
                entity.getEtaSeconds(),
                entity.getTotalDistanceKm(),
                entity.getFailureReason(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
