package com.shippingontheair.delivery.application.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateDeliveryRequest(@NotNull UUID shipmentId) {}
