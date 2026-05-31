package com.shippingontheair.shipment.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shippingontheair.shipment.application.dto.CreateShipmentRequest;
import com.shippingontheair.shipment.application.dto.LocationDto;
import com.shippingontheair.shipment.application.dto.ShipmentResponse;
import com.shippingontheair.shipment.application.dto.UpdateShipmentStatusRequest;
import com.shippingontheair.shipment.domain.model.GeoPoint;
import com.shippingontheair.shipment.domain.model.ScheduleType;
import com.shippingontheair.shipment.domain.model.Shipment;
import com.shippingontheair.shipment.domain.model.ShipmentStatus;
import com.shippingontheair.shipment.domain.repository.ShipmentRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShipmentApplicationServiceTest {

    @Mock
    private ShipmentRepository shipmentRepository;

    private ShipmentApplicationService service;

    @BeforeEach
    void setUp() {
        service = new ShipmentApplicationService(shipmentRepository);
    }

    @Test
    void create_savesAndReturnsShipment() {
        Shipment shipment = Shipment.create(
                new GeoPoint("Origin", 41.0, 28.0),
                new GeoPoint("Dest", 42.0, 29.0),
                2.0,
                ScheduleType.ASAP,
                null);
        when(shipmentRepository.save(any())).thenReturn(shipment);

        CreateShipmentRequest request = new CreateShipmentRequest(
                new LocationDto("Origin", 41.0, 28.0),
                new LocationDto("Dest", 42.0, 29.0),
                2.0,
                ScheduleType.ASAP,
                null);

        ShipmentResponse response = service.create(request);

        assertThat(response.status()).isEqualTo(ShipmentStatus.REQUESTED);
        assertThat(response.weightKg()).isEqualTo(2.0);
        verify(shipmentRepository).save(any());
    }

    @Test
    void get_returnsShipmentWhenFound() {
        UUID id = UUID.randomUUID();
        Shipment shipment = Shipment.create(
                new GeoPoint("A", 1.0, 1.0),
                new GeoPoint("B", 2.0, 2.0),
                1.5,
                ScheduleType.ASAP,
                null);
        when(shipmentRepository.findById(id)).thenReturn(Optional.of(shipment));

        ShipmentResponse response = service.get(id);

        assertThat(response.weightKg()).isEqualTo(1.5);
    }

    @Test
    void get_throwsWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(shipmentRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(id))
                .isInstanceOf(ShipmentNotFoundException.class);
    }

    @Test
    void updateStatus_toDispatched() {
        UUID id = UUID.randomUUID();
        Shipment shipment = Shipment.create(
                new GeoPoint("A", 1.0, 1.0),
                new GeoPoint("B", 2.0, 2.0),
                1.0,
                ScheduleType.ASAP,
                null);
        when(shipmentRepository.findById(id)).thenReturn(Optional.of(shipment));
        when(shipmentRepository.save(any())).thenReturn(shipment);

        ShipmentResponse response = service.updateStatus(id, new UpdateShipmentStatusRequest(ShipmentStatus.DISPATCHED, null));

        assertThat(response.status()).isEqualTo(ShipmentStatus.DISPATCHED);
    }

    @Test
    void updateStatus_toDelivered() {
        UUID id = UUID.randomUUID();
        Shipment shipment = Shipment.create(
                new GeoPoint("A", 1.0, 1.0),
                new GeoPoint("B", 2.0, 2.0),
                1.0,
                ScheduleType.ASAP,
                null);
        shipment.markDispatched();
        when(shipmentRepository.findById(id)).thenReturn(Optional.of(shipment));
        when(shipmentRepository.save(any())).thenReturn(shipment);

        ShipmentResponse response = service.updateStatus(id, new UpdateShipmentStatusRequest(ShipmentStatus.DELIVERED, null));

        assertThat(response.status()).isEqualTo(ShipmentStatus.DELIVERED);
    }

    @Test
    void updateStatus_toFailed_withReason() {
        UUID id = UUID.randomUUID();
        Shipment shipment = Shipment.create(
                new GeoPoint("A", 1.0, 1.0),
                new GeoPoint("B", 2.0, 2.0),
                1.0,
                ScheduleType.ASAP,
                null);
        when(shipmentRepository.findById(id)).thenReturn(Optional.of(shipment));
        when(shipmentRepository.save(any())).thenReturn(shipment);

        ShipmentResponse response = service.updateStatus(id, new UpdateShipmentStatusRequest(ShipmentStatus.FAILED, "drone failure"));

        assertThat(response.status()).isEqualTo(ShipmentStatus.FAILED);
        assertThat(response.failureReason()).isEqualTo("drone failure");
    }

    @Test
    void updateStatus_toFailed_withoutReason_defaultsToUnknown() {
        UUID id = UUID.randomUUID();
        Shipment shipment = Shipment.create(
                new GeoPoint("A", 1.0, 1.0),
                new GeoPoint("B", 2.0, 2.0),
                1.0,
                ScheduleType.ASAP,
                null);
        when(shipmentRepository.findById(id)).thenReturn(Optional.of(shipment));
        when(shipmentRepository.save(any())).thenReturn(shipment);

        ShipmentResponse response = service.updateStatus(id, new UpdateShipmentStatusRequest(ShipmentStatus.FAILED, null));

        assertThat(response.failureReason()).isEqualTo("unknown");
    }

    @Test
    void updateStatus_throwsOnUnsupportedTransition() {
        UUID id = UUID.randomUUID();
        Shipment shipment = Shipment.create(
                new GeoPoint("A", 1.0, 1.0),
                new GeoPoint("B", 2.0, 2.0),
                1.0,
                ScheduleType.ASAP,
                null);
        when(shipmentRepository.findById(id)).thenReturn(Optional.of(shipment));

        assertThatThrownBy(() -> service.updateStatus(id, new UpdateShipmentStatusRequest(ShipmentStatus.REQUESTED, null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("unsupported status transition");
    }

    @Test
    void updateStatus_throwsWhenShipmentNotFound() {
        UUID id = UUID.randomUUID();
        when(shipmentRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateStatus(id, new UpdateShipmentStatusRequest(ShipmentStatus.DISPATCHED, null)))
                .isInstanceOf(ShipmentNotFoundException.class);
    }

    @Test
    void create_scheduled_withFutureScheduledAt() {
        Instant future = Instant.now().plusSeconds(3600);
        Shipment shipment = Shipment.create(
                new GeoPoint("A", 1.0, 1.0),
                new GeoPoint("B", 2.0, 2.0),
                1.0,
                ScheduleType.SCHEDULED,
                future);
        when(shipmentRepository.save(any())).thenReturn(shipment);

        CreateShipmentRequest request = new CreateShipmentRequest(
                new LocationDto("A", 1.0, 1.0),
                new LocationDto("B", 2.0, 2.0),
                1.0,
                ScheduleType.SCHEDULED,
                future);

        ShipmentResponse response = service.create(request);

        assertThat(response.scheduleType()).isEqualTo(ScheduleType.SCHEDULED);
    }
}
