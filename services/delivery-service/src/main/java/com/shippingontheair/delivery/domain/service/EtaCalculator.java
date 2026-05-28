package com.shippingontheair.delivery.domain.service;

public final class EtaCalculator {

    private EtaCalculator() {}

    public static int etaSecondsRemaining(double totalDistanceKm, double progressPercent, double speedKmPerHour) {
        double remainingKm = totalDistanceKm * (1.0 - progressPercent / 100.0);
        if (remainingKm <= 0 || speedKmPerHour <= 0) {
            return 0;
        }
        double hours = remainingKm / speedKmPerHour;
        return (int) Math.ceil(hours * 3600);
    }

    public static double haversineKm(GeoCoordinates from, GeoCoordinates to) {
        double earthRadiusKm = 6371.0;
        double dLat = Math.toRadians(to.latitude() - from.latitude());
        double dLon = Math.toRadians(to.longitude() - from.longitude());
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(from.latitude())) * Math.cos(Math.toRadians(to.latitude()))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadiusKm * c;
    }

    public record GeoCoordinates(double latitude, double longitude) {}
}
