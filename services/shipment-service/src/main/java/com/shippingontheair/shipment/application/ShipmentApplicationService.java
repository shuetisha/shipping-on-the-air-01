package com.shippingontheair.shipment.application;

import com.shippingontheair.shipment.application.dto.CreateShipmentRequest;
import com.shippingontheair.shipment.application.dto.ShipmentResponse;
import com.shippingontheair.shipment.application.dto.UpdateShipmentStatusRequest;
import com.shippingontheair.shipment.domain.model.GeoPoint;
import com.shippingontheair.shipment.domain.model.Shipment;
import com.shippingontheair.shipment.domain.model.ShipmentStatus;
import com.shippingontheair.shipment.domain.repository.ShipmentRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShipmentApplicationService {

    private final ShipmentRepository shipmentRepository;

    public ShipmentApplicationService(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    @Transactional
    public ShipmentResponse create(CreateShipmentRequest request) {
        Shipment shipment = Shipment.create(
                new GeoPoint(request.origin().label(), request.origin().latitude(), request.origin().longitude()),
                new GeoPoint(request.destination().label(), request.destination().latitude(), request.destination().longitude()),
                request.weightKg(),
                request.scheduleType(),
                request.scheduledAt());
        return ShipmentResponse.from(shipmentRepository.save(shipment));
    }

    @Transactional(readOnly = true)
    public ShipmentResponse get(UUID id) {
        return shipmentRepository.findById(id)
                .map(ShipmentResponse::from)
                .orElseThrow(() -> new ShipmentNotFoundException(id));
    }

    @Transactional
    public ShipmentResponse updateStatus(UUID id, UpdateShipmentStatusRequest request) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ShipmentNotFoundException(id));
        switch (request.status()) {
            case DISPATCHED -> shipment.markDispatched();
            case DELIVERED -> shipment.markDelivered();
            case FAILED -> shipment.markFailed(request.failureReason() != null ? request.failureReason() : "unknown");
            default -> throw new IllegalArgumentException("unsupported status transition: " + request.status());
        }
        return ShipmentResponse.from(shipmentRepository.save(shipment));
    }
}
