package com.shippingontheair.fleet.application.dto;

import jakarta.validation.constraints.Positive;

public record ReserveDroneRequest(@Positive double requiredPayloadKg) {}
