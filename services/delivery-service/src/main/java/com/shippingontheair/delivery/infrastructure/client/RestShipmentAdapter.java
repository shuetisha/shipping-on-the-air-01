package com.shippingontheair.delivery.infrastructure.client;

import com.shippingontheair.delivery.application.port.ShipmentPort;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class RestShipmentAdapter implements ShipmentPort {

    private final RestClient restClient;

    public RestShipmentAdapter(@Value("${shipment-service.url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    public ShipmentView getShipment(UUID shipmentId) {
        ShipmentResponse response = restClient.get()
                .uri("/shipments/{id}", shipmentId)
                .retrieve()
                .body(ShipmentResponse.class);
        if (response == null) {
            throw new IllegalStateException("empty shipment response");
        }
        return new ShipmentView(
                response.id(),
                response.status(),
                response.weightKg(),
                new Location(response.origin().label(), response.origin().latitude(), response.origin().longitude()),
                new Location(response.destination().label(), response.destination().latitude(), response.destination().longitude()));
    }

    @Override
    public void markDispatched(UUID shipmentId) {
        patchStatus(shipmentId, "DISPATCHED", null);
    }

    @Override
    public void markDelivered(UUID shipmentId) {
        patchStatus(shipmentId, "DELIVERED", null);
    }

    @Override
    public void markFailed(UUID shipmentId, String reason) {
        patchStatus(shipmentId, "FAILED", reason);
    }

    private void patchStatus(UUID shipmentId, String status, String reason) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", status);
        if (reason != null) {
            body.put("failureReason", reason);
        }
        restClient.patch()
                .uri("/shipments/{id}/status", shipmentId)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }

    private record ShipmentResponse(
            UUID id,
            String status,
            double weightKg,
            LocationResponse origin,
            LocationResponse destination) {}

    private record LocationResponse(String label, double latitude, double longitude) {}
}
