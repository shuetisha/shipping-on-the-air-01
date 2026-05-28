package com.shippingontheair.shipment.application.dto;

import com.shippingontheair.shipment.domain.model.ScheduleType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Instant;

public record CreateShipmentRequest(
        @Valid @NotNull LocationDto origin,
        @Valid @NotNull LocationDto destination,
        @Positive double weightKg,
        @NotNull ScheduleType scheduleType,
        Instant scheduledAt) {}
