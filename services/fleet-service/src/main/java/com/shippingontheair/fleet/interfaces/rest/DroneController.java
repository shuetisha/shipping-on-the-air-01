package com.shippingontheair.fleet.interfaces.rest;

import com.shippingontheair.fleet.application.DroneApplicationService;
import com.shippingontheair.fleet.application.dto.DroneResponse;
import com.shippingontheair.fleet.application.dto.RegisterDroneRequest;
import com.shippingontheair.fleet.application.dto.ReserveDroneRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/drones")
public class DroneController {

    private final DroneApplicationService droneApplicationService;

    public DroneController(DroneApplicationService droneApplicationService) {
        this.droneApplicationService = droneApplicationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DroneResponse register(@Valid @RequestBody RegisterDroneRequest request) {
        return droneApplicationService.register(request);
    }

    @GetMapping
    public List<DroneResponse> list(@RequestParam(required = false) String status) {
        if ("AVAILABLE".equalsIgnoreCase(status)) {
            return droneApplicationService.listAvailable();
        }
        throw new IllegalArgumentException("unsupported query; use status=AVAILABLE");
    }

    @GetMapping("/{id}")
    public DroneResponse get(@PathVariable("id") UUID id) {
        return droneApplicationService.get(id);
    }

    @PostMapping("/{id}/reserve")
    public DroneResponse reserve(@PathVariable("id") UUID id, @Valid @RequestBody ReserveDroneRequest request) {
        return droneApplicationService.reserve(id, request);
    }

    @PostMapping("/{id}/release")
    public DroneResponse release(@PathVariable("id") UUID id) {
        return droneApplicationService.release(id);
    }
}
