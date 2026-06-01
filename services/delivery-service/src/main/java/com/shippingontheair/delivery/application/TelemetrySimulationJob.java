package com.shippingontheair.delivery.application;

import com.shippingontheair.delivery.application.port.FleetPort;
import com.shippingontheair.delivery.application.port.ShipmentPort;
import com.shippingontheair.delivery.domain.model.Delivery;
import com.shippingontheair.delivery.domain.model.DeliveryStatus;
import com.shippingontheair.delivery.domain.repository.DeliveryRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TelemetrySimulationJob {

    private static final Logger log = LoggerFactory.getLogger(TelemetrySimulationJob.class);

    private final DeliveryRepository deliveryRepository;
    private final ShipmentPort shipmentPort;
    private final FleetPort fleetPort;
    private final double speedKmPerHour;
    private final double progressIncrement;

    public TelemetrySimulationJob(
            DeliveryRepository deliveryRepository,
            ShipmentPort shipmentPort,
            FleetPort fleetPort,
            @Value("${delivery.simulation.speed-km-per-hour:36}") double speedKmPerHour,
            @Value("${delivery.simulation.tick-ms:3000}") long tickMs) {
        this.deliveryRepository = deliveryRepository;
        this.shipmentPort = shipmentPort;
        this.fleetPort = fleetPort;
        this.speedKmPerHour = speedKmPerHour;
        this.progressIncrement = 100.0 / Math.max(1, (60_000 / tickMs));
    }

    @Scheduled(fixedDelayString = "${delivery.simulation.tick-ms:3000}")
    @Transactional
    public void tick() {
        List<Delivery> inTransit = deliveryRepository.findByStatus(DeliveryStatus.IN_TRANSIT);
        if (inTransit.isEmpty()) {
            return;
        }
        log.debug("Telemetry tick: {} delivery/deliveries in transit", inTransit.size());
        for (Delivery delivery : inTransit) {
            delivery.advanceProgress(progressIncrement, speedKmPerHour);
            deliveryRepository.save(delivery);
            log.debug("Delivery progress: id={}, progress={}%, eta={}s",
                    delivery.getId(),
                    String.format("%.1f", delivery.getProgressPercent()),
                    delivery.getEtaSeconds());
            if (delivery.getStatus() == DeliveryStatus.DELIVERED) {
                log.info("Delivery completed: id={}, shipmentId={}, droneId={}",
                        delivery.getId(), delivery.getShipmentId(), delivery.getDroneId());
                shipmentPort.markDelivered(delivery.getShipmentId());
                fleetPort.release(delivery.getDroneId());
            }
        }
    }
}
