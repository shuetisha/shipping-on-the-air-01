package com.shippingontheair.shipment.application;

import com.shippingontheair.shipment.application.dto.CreateShipmentRequest;
import com.shippingontheair.shipment.application.dto.ShipmentResponse;
import com.shippingontheair.shipment.application.dto.UpdateShipmentStatusRequest;
import com.shippingontheair.shipment.domain.model.GeoPoint;
import com.shippingontheair.shipment.domain.model.Shipment;
import com.shippingontheair.shipment.domain.model.ShipmentStatus;
import com.shippingontheair.shipment.domain.repository.ShipmentRepository;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShipmentApplicationService {

    private static final Logger log = LoggerFactory.getLogger(ShipmentApplicationService.class);

    private final ShipmentRepository shipmentRepository;

    public ShipmentApplicationService(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    @Transactional
    public ShipmentResponse create(CreateShipmentRequest request) {
        log.info("Creating shipment: origin={}, destination={}, weightKg={}, scheduleType={}",
                request.origin().label(), request.destination().label(),
                request.weightKg(), request.scheduleType());
        Shipment shipment = Shipment.create(
                new GeoPoint(request.origin().label(), request.origin().latitude(), request.origin().longitude()),
                new GeoPoint(request.destination().label(), request.destination().latitude(), request.destination().longitude()),
                request.weightKg(),
                request.scheduleType(),
                request.scheduledAt());
        Shipment saved = shipmentRepository.save(shipment);
        log.info("Shipment created: id={}, status={}", saved.getId(), saved.getStatus());
        return ShipmentResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public ShipmentResponse get(UUID id) {
        log.debug("Fetching shipment: id={}", id);
        return shipmentRepository.findById(id)
                .map(ShipmentResponse::from)
                .orElseThrow(() -> new ShipmentNotFoundException(id));
    }

    @Transactional
    public ShipmentResponse updateStatus(UUID id, UpdateShipmentStatusRequest request) {
        log.info("Updating shipment status: id={}, newStatus={}", id, request.status());
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ShipmentNotFoundException(id));
        switch (request.status()) {
            case DISPATCHED -> shipment.markDispatched();
            case DELIVERED -> shipment.markDelivered();
            case FAILED -> {
                String reason = request.failureReason() != null ? request.failureReason() : "unknown";
                log.warn("Marking shipment as FAILED: id={}, reason={}", id, reason);
                shipment.markFailed(reason);
            }
            default -> throw new IllegalArgumentException("unsupported status transition: " + request.status());
        }
        Shipment saved = shipmentRepository.save(shipment);
        log.info("Shipment status updated: id={}, status={}", saved.getId(), saved.getStatus());
        return ShipmentResponse.from(saved);
    }
}
