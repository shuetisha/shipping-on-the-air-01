package com.shippingontheair.shipment.application.dto;

import com.shippingontheair.shipment.domain.model.ShipmentStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateShipmentStatusRequest(
        @NotNull ShipmentStatus status,
        String failureReason) {}
