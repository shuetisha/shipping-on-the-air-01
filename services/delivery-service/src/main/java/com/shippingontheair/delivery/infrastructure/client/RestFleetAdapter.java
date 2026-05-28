package com.shippingontheair.delivery.infrastructure.client;

import com.shippingontheair.delivery.application.port.FleetPort;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class RestFleetAdapter implements FleetPort {

    private final RestClient restClient;

    public RestFleetAdapter(@Value("${fleet-service.url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    public List<DroneView> listAvailable() {
        DroneResponse[] drones = restClient.get()
                .uri("/drones?status=AVAILABLE")
                .retrieve()
                .body(DroneResponse[].class);
        if (drones == null) {
            return List.of();
        }
        return Arrays.stream(drones)
                .map(d -> new DroneView(d.id(), d.name(), d.maxPayloadKg(), d.latitude(), d.longitude()))
                .toList();
    }

    @Override
    public void reserve(UUID droneId, double requiredPayloadKg) {
        restClient.post()
                .uri("/drones/{id}/reserve", droneId)
                .body(Map.of("requiredPayloadKg", requiredPayloadKg))
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void release(UUID droneId) {
        restClient.post()
                .uri("/drones/{id}/release", droneId)
                .retrieve()
                .toBodilessEntity();
    }

    private record DroneResponse(UUID id, String name, double maxPayloadKg, double latitude, double longitude) {}
}
