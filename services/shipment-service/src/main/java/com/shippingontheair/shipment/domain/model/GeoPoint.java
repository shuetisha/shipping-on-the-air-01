package com.shippingontheair.shipment.domain.model;

public record GeoPoint(String label, double latitude, double longitude) {

    public GeoPoint {
        if (label == null || label.isBlank()) {
            throw new IllegalArgumentException("label is required");
        }
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("latitude out of range");
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("longitude out of range");
        }
    }
}
