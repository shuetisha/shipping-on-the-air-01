package com.shippingontheair.shipment.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LocationDto(
        @NotBlank String label,
        @NotNull Double latitude,
        @NotNull Double longitude) {}
