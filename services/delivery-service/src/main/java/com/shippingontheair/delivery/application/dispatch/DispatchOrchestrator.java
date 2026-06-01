package com.shippingontheair.delivery.application.dispatch;

import com.shippingontheair.delivery.application.port.FleetPort;
import com.shippingontheair.delivery.application.port.ShipmentPort;
import com.shippingontheair.delivery.domain.model.Delivery;
import com.shippingontheair.delivery.domain.model.GeoPoint;
import java.util.Comparator;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application-level dispatch orchestration (Dispatch bounded context in delivery-service).
 */
public class DispatchOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(DispatchOrchestrator.class);

    private final ShipmentPort shipmentPort;
    private final FleetPort fleetPort;
    private final double speedKmPerHour;

    public DispatchOrchestrator(ShipmentPort shipmentPort, FleetPort fleetPort, double speedKmPerHour) {
        this.shipmentPort = shipmentPort;
        this.fleetPort = fleetPort;
        this.speedKmPerHour = speedKmPerHour;
    }

    public DispatchResult dispatch(UUID shipmentId) {
        log.info("Dispatching shipment: id={}", shipmentId);
        ShipmentPort.ShipmentView shipment = shipmentPort.getShipment(shipmentId);
        if (!"REQUESTED".equals(shipment.status())) {
            throw new IllegalStateException("shipment must be REQUESTED, was: " + shipment.status());
        }

        FleetPort.DroneView drone = fleetPort.listAvailable().stream()
                .filter(d -> d.maxPayloadKg() >= shipment.weightKg())
                .min(Comparator.comparingDouble(d -> distance(
                        d.latitude(), d.longitude(),
                        shipment.origin().latitude(), shipment.origin().longitude())))
                .orElseThrow(() -> new DispatchException("no available drone with sufficient capacity"));

        log.info("Selected drone: id={}, name={}, maxPayloadKg={}", drone.id(), drone.name(), drone.maxPayloadKg());

        UUID reservedDroneId = null;
        try {
            fleetPort.reserve(drone.id(), shipment.weightKg());
            reservedDroneId = drone.id();
            log.debug("Drone reserved: droneId={}", reservedDroneId);

            Delivery delivery = Delivery.start(
                    shipmentId,
                    drone.id(),
                    new GeoPoint(shipment.origin().latitude(), shipment.origin().longitude()),
                    new GeoPoint(shipment.destination().latitude(), shipment.destination().longitude()),
                    speedKmPerHour);

            log.info("Delivery started: id={}, shipmentId={}, droneId={}, distanceKm={}, etaSeconds={}",
                    delivery.getId(), shipmentId, drone.id(),
                    String.format("%.2f", delivery.getTotalDistanceKm()), delivery.getEtaSeconds());

            shipmentPort.markDispatched(shipmentId);
            return new DispatchResult(delivery);
        } catch (RuntimeException ex) {
            log.error("Dispatch failed for shipment {}: {}", shipmentId, ex.getMessage());
            if (reservedDroneId != null) {
                try {
                    fleetPort.release(reservedDroneId);
                    log.info("Rolled back drone reservation: droneId={}", reservedDroneId);
                } catch (RuntimeException ignored) {
                    log.warn("Failed to release drone during rollback: droneId={}", reservedDroneId);
                }
            }
            try {
                shipmentPort.markFailed(shipmentId, ex.getMessage());
            } catch (RuntimeException ignored) {
                log.warn("Failed to mark shipment as failed during rollback: shipmentId={}", shipmentId);
            }
            throw ex instanceof DispatchException ? ex : new DispatchException(ex.getMessage(), ex);
        }
    }

    private static double distance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;
        return dLat * dLat + dLon * dLon;
    }

    public record DispatchResult(Delivery delivery) {}
}
