package com.shippingontheair.shipment.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ShipmentTest {

    @Test
    void createsRequestedShipment() {
        Shipment shipment = Shipment.create(
                new GeoPoint("A", 41.0, 12.0),
                new GeoPoint("B", 42.0, 13.0),
                2.5,
                ScheduleType.ASAP,
                null);
        assertEquals(ShipmentStatus.REQUESTED, shipment.getStatus());
    }

    @Test
    void rejectsNonPositiveWeight() {
        assertThrows(IllegalArgumentException.class, () -> Shipment.create(
                new GeoPoint("A", 41.0, 12.0),
                new GeoPoint("B", 42.0, 13.0),
                0,
                ScheduleType.ASAP,
                null));
    }

    @Test
    void transitionsToDispatchedThenDelivered() {
        Shipment shipment = Shipment.create(
                new GeoPoint("A", 41.0, 12.0),
                new GeoPoint("B", 42.0, 13.0),
                1,
                ScheduleType.ASAP,
                null);
        shipment.markDispatched();
        shipment.markDelivered();
        assertEquals(ShipmentStatus.DELIVERED, shipment.getStatus());
    }
}
