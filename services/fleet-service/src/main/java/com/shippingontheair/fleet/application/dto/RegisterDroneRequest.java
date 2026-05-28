package com.shippingontheair.fleet.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RegisterDroneRequest(
        @NotBlank String name,
        @Positive double maxPayloadKg,
        @NotNull Double latitude,
        @NotNull Double longitude) {}
