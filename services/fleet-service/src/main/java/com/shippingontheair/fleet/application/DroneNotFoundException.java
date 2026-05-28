package com.shippingontheair.fleet.application;

import java.util.UUID;

public class DroneNotFoundException extends RuntimeException {

    public DroneNotFoundException(UUID id) {
        super("Drone not found: " + id);
    }
}
