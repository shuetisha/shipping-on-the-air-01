package com.shippingontheair.delivery.application.dispatch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shippingontheair.delivery.application.port.FleetPort;
import com.shippingontheair.delivery.application.port.ShipmentPort;
import com.shippingontheair.delivery.domain.model.DeliveryStatus;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DispatchOrchestratorTest {

    @Mock
    private ShipmentPort shipmentPort;

    @Mock
    private FleetPort fleetPort;

    private DispatchOrchestrator orchestrator;

    private static final double SPEED = 60.0;
    private final UUID shipmentId = UUID.randomUUID();
    private final UUID droneId = UUID.randomUUID();

    private final ShipmentPort.Location origin = new ShipmentPort.Location("Istanbul", 41.0, 28.0);
    private final ShipmentPort.Location destination = new ShipmentPort.Location("Ankara", 40.0, 32.0);
    private final ShipmentPort.ShipmentView requestedShipment =
            new ShipmentPort.ShipmentView(shipmentId, "REQUESTED", 3.0, origin, destination);

    @BeforeEach
    void setUp() {
        orchestrator = new DispatchOrchestrator(shipmentPort, fleetPort, SPEED);
    }

    @Test
    void dispatch_succeedsWithAvailableDrone() {
        FleetPort.DroneView drone = new FleetPort.DroneView(droneId, "Alpha", 5.0, 41.0, 28.0);
        when(shipmentPort.getShipment(shipmentId)).thenReturn(requestedShipment);
        when(fleetPort.listAvailable()).thenReturn(List.of(drone));

        DispatchOrchestrator.DispatchResult result = orchestrator.dispatch(shipmentId);

        assertThat(result.delivery()).isNotNull();
        assertThat(result.delivery().getShipmentId()).isEqualTo(shipmentId);
        assertThat(result.delivery().getDroneId()).isEqualTo(droneId);
        assertThat(result.delivery().getStatus()).isEqualTo(DeliveryStatus.IN_TRANSIT);

        verify(fleetPort).reserve(droneId, 3.0);
        verify(shipmentPort).markDispatched(shipmentId);
    }

    @Test
    void dispatch_selectsClosestDrone() {
        UUID nearDroneId = UUID.randomUUID();
        UUID farDroneId = UUID.randomUUID();
        FleetPort.DroneView nearDrone = new FleetPort.DroneView(nearDroneId, "Near", 5.0, 41.0, 28.0);
        FleetPort.DroneView farDrone = new FleetPort.DroneView(farDroneId, "Far", 5.0, 50.0, 50.0);

        when(shipmentPort.getShipment(shipmentId)).thenReturn(requestedShipment);
        when(fleetPort.listAvailable()).thenReturn(List.of(farDrone, nearDrone));

        DispatchOrchestrator.DispatchResult result = orchestrator.dispatch(shipmentId);

        assertThat(result.delivery().getDroneId()).isEqualTo(nearDroneId);
    }

    @Test
    void dispatch_throwsWhenShipmentNotRequested() {
        ShipmentPort.ShipmentView dispatched =
                new ShipmentPort.ShipmentView(shipmentId, "DISPATCHED", 3.0, origin, destination);
        when(shipmentPort.getShipment(shipmentId)).thenReturn(dispatched);

        assertThatThrownBy(() -> orchestrator.dispatch(shipmentId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("shipment must be REQUESTED");
    }

    @Test
    void dispatch_throwsWhenNoDroneAvailable() {
        when(shipmentPort.getShipment(shipmentId)).thenReturn(requestedShipment);
        when(fleetPort.listAvailable()).thenReturn(List.of());

        assertThatThrownBy(() -> orchestrator.dispatch(shipmentId))
                .isInstanceOf(DispatchException.class)
                .hasMessageContaining("no available drone");
    }

    @Test
    void dispatch_throwsWhenNoDroneHasSufficientCapacity() {
        FleetPort.DroneView smallDrone = new FleetPort.DroneView(droneId, "Small", 1.0, 41.0, 28.0);
        when(shipmentPort.getShipment(shipmentId)).thenReturn(requestedShipment);
        when(fleetPort.listAvailable()).thenReturn(List.of(smallDrone));

        assertThatThrownBy(() -> orchestrator.dispatch(shipmentId))
                .isInstanceOf(DispatchException.class)
                .hasMessageContaining("no available drone with sufficient capacity");
    }

    @Test
    void dispatch_rollsBackReservationOnFailure() {
        FleetPort.DroneView drone = new FleetPort.DroneView(droneId, "Alpha", 5.0, 41.0, 28.0);
        when(shipmentPort.getShipment(shipmentId)).thenReturn(requestedShipment);
        when(fleetPort.listAvailable()).thenReturn(List.of(drone));
        doThrow(new RuntimeException("shipment service down")).when(shipmentPort).markDispatched(shipmentId);

        assertThatThrownBy(() -> orchestrator.dispatch(shipmentId))
                .isInstanceOf(DispatchException.class);

        verify(fleetPort).release(droneId);
        verify(shipmentPort).markFailed(eq(shipmentId), any());
    }

    @Test
    void dispatch_marksShipmentFailedWhenReservationFails() {
        FleetPort.DroneView drone = new FleetPort.DroneView(droneId, "Alpha", 5.0, 41.0, 28.0);
        when(shipmentPort.getShipment(shipmentId)).thenReturn(requestedShipment);
        when(fleetPort.listAvailable()).thenReturn(List.of(drone));
        doThrow(new RuntimeException("fleet service error")).when(fleetPort).reserve(any(), any(Double.class));

        assertThatThrownBy(() -> orchestrator.dispatch(shipmentId))
                .isInstanceOf(DispatchException.class);

        verify(fleetPort, never()).release(any());
        verify(shipmentPort).markFailed(eq(shipmentId), any());
    }
}
