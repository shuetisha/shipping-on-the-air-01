package com.shippingontheair.delivery.interfaces.rest;

import com.shippingontheair.delivery.application.DeliveryApplicationService;
import com.shippingontheair.delivery.application.dto.CreateDeliveryRequest;
import com.shippingontheair.delivery.application.dto.DeliveryResponse;
import com.shippingontheair.delivery.application.dto.TrackingResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/deliveries")
public class DeliveryController {

    private final DeliveryApplicationService deliveryApplicationService;

    public DeliveryController(DeliveryApplicationService deliveryApplicationService) {
        this.deliveryApplicationService = deliveryApplicationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DeliveryResponse create(@Valid @RequestBody CreateDeliveryRequest request) {
        return deliveryApplicationService.startDelivery(request);
    }

    @GetMapping("/{id}")
    public DeliveryResponse get(@PathVariable("id") UUID id) {
        return deliveryApplicationService.get(id);
    }

    @GetMapping("/{id}/tracking")
    public TrackingResponse track(@PathVariable("id") UUID id) {
        return deliveryApplicationService.track(id);
    }
}
