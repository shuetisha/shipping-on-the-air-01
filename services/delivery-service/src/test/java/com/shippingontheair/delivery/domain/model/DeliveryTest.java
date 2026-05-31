package com.shippingontheair.delivery.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class DeliveryTest {

    private static final GeoPoint ISTANBUL = new GeoPoint(41.0082, 28.9784);
    private static final GeoPoint ANKARA = new GeoPoint(39.9334, 32.8597);
    private static final double SPEED_KMH = 60.0;

    @Test
    void start_createsInTransitDelivery() {
        UUID shipmentId = UUID.randomUUID();
        UUID droneId = UUID.randomUUID();

        Delivery delivery = Delivery.start(shipmentId, droneId, ISTANBUL, ANKARA, SPEED_KMH);

        assertThat(delivery.getId()).isNotNull();
        assertThat(delivery.getShipmentId()).isEqualTo(shipmentId);
        assertThat(delivery.getDroneId()).isEqualTo(droneId);
        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.IN_TRANSIT);
        assertThat(delivery.getProgressPercent()).isEqualTo(0.0);
        assertThat(delivery.getEtaSeconds()).isPositive();
        assertThat(delivery.getTotalDistanceKm()).isPositive();
        assertThat(delivery.getCurrentPosition()).isEqualTo(ISTANBUL);
        assertThat(delivery.getFailureReason()).isNull();
    }

    @Test
    void advanceProgress_updatesPositionAndEta() {
        Delivery delivery = Delivery.start(UUID.randomUUID(), UUID.randomUUID(), ISTANBUL, ANKARA, SPEED_KMH);

        delivery.advanceProgress(50.0, SPEED_KMH);

        assertThat(delivery.getProgressPercent()).isEqualTo(50.0);
        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.IN_TRANSIT);
        assertThat(delivery.getCurrentPosition().latitude())
                .isBetween(ANKARA.latitude(), ISTANBUL.latitude());
    }

    @Test
    void advanceProgress_to100_transitionsToDelivered() {
        Delivery delivery = Delivery.start(UUID.randomUUID(), UUID.randomUUID(), ISTANBUL, ANKARA, SPEED_KMH);

        delivery.advanceProgress(100.0, SPEED_KMH);

        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.DELIVERED);
        assertThat(delivery.getProgressPercent()).isEqualTo(100.0);
        assertThat(delivery.getEtaSeconds()).isEqualTo(0);
        assertThat(delivery.getCurrentPosition()).isEqualTo(ANKARA);
    }

    @Test
    void advanceProgress_cappedAt100() {
        Delivery delivery = Delivery.start(UUID.randomUUID(), UUID.randomUUID(), ISTANBUL, ANKARA, SPEED_KMH);

        delivery.advanceProgress(150.0, SPEED_KMH);

        assertThat(delivery.getProgressPercent()).isEqualTo(100.0);
    }

    @Test
    void advanceProgress_doesNothingWhenNotInTransit() {
        Delivery delivery = Delivery.start(UUID.randomUUID(), UUID.randomUUID(), ISTANBUL, ANKARA, SPEED_KMH);
        delivery.markFailed("error");

        double progressBefore = delivery.getProgressPercent();
        delivery.advanceProgress(10.0, SPEED_KMH);

        assertThat(delivery.getProgressPercent()).isEqualTo(progressBefore);
        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.FAILED);
    }

    @Test
    void markFailed_setsStatusAndReason() {
        Delivery delivery = Delivery.start(UUID.randomUUID(), UUID.randomUUID(), ISTANBUL, ANKARA, SPEED_KMH);

        delivery.markFailed("drone battery dead");

        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.FAILED);
        assertThat(delivery.getFailureReason()).isEqualTo("drone battery dead");
    }

    @Test
    void advanceProgress_multipleIncrements_accumulatesProgress() {
        Delivery delivery = Delivery.start(UUID.randomUUID(), UUID.randomUUID(), ISTANBUL, ANKARA, SPEED_KMH);

        delivery.advanceProgress(30.0, SPEED_KMH);
        delivery.advanceProgress(30.0, SPEED_KMH);

        assertThat(delivery.getProgressPercent()).isEqualTo(60.0);
        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.IN_TRANSIT);
    }

    @Test
    void restore_rebuildsDelivery() {
        UUID id = UUID.randomUUID();
        UUID shipmentId = UUID.randomUUID();
        UUID droneId = UUID.randomUUID();
        java.time.Instant now = java.time.Instant.now();

        Delivery delivery = Delivery.restore(
                id, shipmentId, droneId,
                DeliveryStatus.IN_TRANSIT,
                ISTANBUL, ANKARA, ISTANBUL,
                25.0, 300, 400.0,
                null, now, now);

        assertThat(delivery.getId()).isEqualTo(id);
        assertThat(delivery.getShipmentId()).isEqualTo(shipmentId);
        assertThat(delivery.getDroneId()).isEqualTo(droneId);
        assertThat(delivery.getProgressPercent()).isEqualTo(25.0);
        assertThat(delivery.getEtaSeconds()).isEqualTo(300);
        assertThat(delivery.getTotalDistanceKm()).isEqualTo(400.0);
    }
}
