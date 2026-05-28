package com.shippingontheair.delivery.application;

import com.shippingontheair.delivery.application.dispatch.DispatchOrchestrator;
import com.shippingontheair.delivery.application.dto.CreateDeliveryRequest;
import com.shippingontheair.delivery.application.dto.DeliveryResponse;
import com.shippingontheair.delivery.application.dto.TrackingResponse;
import com.shippingontheair.delivery.application.port.FleetPort;
import com.shippingontheair.delivery.application.port.ShipmentPort;
import com.shippingontheair.delivery.domain.model.Delivery;
import com.shippingontheair.delivery.domain.repository.DeliveryRepository;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeliveryApplicationService {

    private final DeliveryRepository deliveryRepository;
    private final DispatchOrchestrator dispatchOrchestrator;

    public DeliveryApplicationService(
            DeliveryRepository deliveryRepository,
            ShipmentPort shipmentPort,
            FleetPort fleetPort,
            @Value("${delivery.simulation.speed-km-per-hour:36}") double speedKmPerHour) {
        this.deliveryRepository = deliveryRepository;
        this.dispatchOrchestrator = new DispatchOrchestrator(shipmentPort, fleetPort, speedKmPerHour);
    }

    @Transactional
    public DeliveryResponse startDelivery(CreateDeliveryRequest request) {
        Delivery delivery = dispatchOrchestrator.dispatch(request.shipmentId()).delivery();
        return DeliveryResponse.from(deliveryRepository.save(delivery));
    }

    @Transactional(readOnly = true)
    public DeliveryResponse get(UUID id) {
        return deliveryRepository.findById(id)
                .map(DeliveryResponse::from)
                .orElseThrow(() -> new DeliveryNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public TrackingResponse track(UUID id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new DeliveryNotFoundException(id));
        return TrackingResponse.from(delivery);
    }
}
