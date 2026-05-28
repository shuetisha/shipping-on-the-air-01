package com.shippingontheair.shipment.interfaces.rest;

import com.shippingontheair.shipment.application.ShipmentApplicationService;
import com.shippingontheair.shipment.application.dto.CreateShipmentRequest;
import com.shippingontheair.shipment.application.dto.ShipmentResponse;
import com.shippingontheair.shipment.application.dto.UpdateShipmentStatusRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shipments")
public class ShipmentController {

    private final ShipmentApplicationService shipmentApplicationService;

    public ShipmentController(ShipmentApplicationService shipmentApplicationService) {
        this.shipmentApplicationService = shipmentApplicationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShipmentResponse create(@Valid @RequestBody CreateShipmentRequest request) {
        return shipmentApplicationService.create(request);
    }

    @GetMapping("/{id}")
    public ShipmentResponse get(@PathVariable("id") UUID id) {
        return shipmentApplicationService.get(id);
    }

    @PatchMapping("/{id}/status")
    public ShipmentResponse updateStatus(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateShipmentStatusRequest request) {
        return shipmentApplicationService.updateStatus(id, request);
    }
}
