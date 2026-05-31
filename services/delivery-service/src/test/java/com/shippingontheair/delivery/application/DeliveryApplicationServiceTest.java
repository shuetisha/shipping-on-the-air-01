package com.shippingontheair.delivery.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.shippingontheair.delivery.application.dto.CreateDeliveryRequest;
import com.shippingontheair.delivery.application.dto.DeliveryResponse;
import com.shippingontheair.delivery.application.dto.TrackingResponse;
import com.shippingontheair.delivery.application.port.FleetPort;
import com.shippingontheair.delivery.application.port.ShipmentPort;
import com.shippingontheair.delivery.domain.model.Delivery;
import com.shippingontheair.delivery.domain.model.DeliveryStatus;
import com.shippingontheair.delivery.domain.model.GeoPoint;
import com.shippingontheair.delivery.domain.repository.DeliveryRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeliveryApplicationServiceTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private ShipmentPort shipmentPort;

    @Mock
    private FleetPort fleetPort;

    private DeliveryApplicationService service;

    private final UUID shipmentId = UUID.randomUUID();
    private final UUID droneId = UUID.randomUUID();

    private final ShipmentPort.Location origin = new ShipmentPort.Location("Istanbul", 41.0, 28.0);
    private final ShipmentPort.Location destination = new ShipmentPort.Location("Ankara", 40.0, 32.0);
    private final ShipmentPort.ShipmentView requestedShipment =
            new ShipmentPort.ShipmentView(shipmentId, "REQUESTED", 3.0, origin, destination);
    private final FleetPort.DroneView drone = new FleetPort.DroneView(droneId, "Alpha", 5.0, 41.0, 28.0);

    @BeforeEach
    void setUp() {
        service = new DeliveryApplicationService(deliveryRepository, shipmentPort, fleetPort, 60.0);
    }

    @Test
    void startDelivery_createsAndSavesDelivery() {
        when(shipmentPort.getShipment(shipmentId)).thenReturn(requestedShipment);
        when(fleetPort.listAvailable()).thenReturn(List.of(drone));

        Delivery delivery = Delivery.start(
                shipmentId, droneId,
                new GeoPoint(41.0, 28.0),
                new GeoPoint(40.0, 32.0),
                60.0);
        when(deliveryRepository.save(any())).thenReturn(delivery);

        DeliveryResponse response = service.startDelivery(new CreateDeliveryRequest(shipmentId));

        assertThat(response.shipmentId()).isEqualTo(shipmentId);
        assertThat(response.droneId()).isEqualTo(droneId);
        assertThat(response.status()).isEqualTo(DeliveryStatus.IN_TRANSIT);
    }

    @Test
    void get_returnsDeliveryWhenFound() {
        UUID id = UUID.randomUUID();
        Delivery delivery = Delivery.start(
                shipmentId, droneId,
                new GeoPoint(41.0, 28.0),
                new GeoPoint(40.0, 32.0),
                60.0);
        when(deliveryRepository.findById(id)).thenReturn(Optional.of(delivery));

        DeliveryResponse response = service.get(id);

        assertThat(response.shipmentId()).isEqualTo(shipmentId);
    }

    @Test
    void get_throwsWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(deliveryRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(id))
                .isInstanceOf(DeliveryNotFoundException.class);
    }

    @Test
    void track_returnsTrackingDataWhenFound() {
        UUID id = UUID.randomUUID();
        Delivery delivery = Delivery.start(
                shipmentId, droneId,
                new GeoPoint(41.0, 28.0),
                new GeoPoint(40.0, 32.0),
                60.0);
        when(deliveryRepository.findById(id)).thenReturn(Optional.of(delivery));

        TrackingResponse response = service.track(id);

        assertThat(response.deliveryId()).isEqualTo(delivery.getId());
        assertThat(response.phase()).isEqualTo(DeliveryStatus.IN_TRANSIT);
        assertThat(response.progressPercent()).isEqualTo(0.0);
    }

    @Test
    void track_throwsWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(deliveryRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.track(id))
                .isInstanceOf(DeliveryNotFoundException.class);
    }
}
