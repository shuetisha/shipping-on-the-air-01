package com.shippingontheair.delivery.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import com.shippingontheair.delivery.domain.service.EtaCalculator.GeoCoordinates;
import org.junit.jupiter.api.Test;

class EtaCalculatorTest {

    @Test
    void haversineKm_samePoint_returnsZero() {
        GeoCoordinates point = new GeoCoordinates(41.0, 28.0);
        double distance = EtaCalculator.haversineKm(point, point);
        assertThat(distance).isCloseTo(0.0, within(0.001));
    }

    @Test
    void haversineKm_istanbulToAnkara_returnsReasonableDistance() {
        GeoCoordinates istanbul = new GeoCoordinates(41.0082, 28.9784);
        GeoCoordinates ankara = new GeoCoordinates(39.9334, 32.8597);

        double distance = EtaCalculator.haversineKm(istanbul, ankara);

        // Istanbul to Ankara is approximately 350 km
        assertThat(distance).isBetween(340.0, 360.0);
    }

    @Test
    void haversineKm_isSymmetric() {
        GeoCoordinates a = new GeoCoordinates(41.0, 28.0);
        GeoCoordinates b = new GeoCoordinates(39.0, 32.0);

        assertThat(EtaCalculator.haversineKm(a, b))
                .isCloseTo(EtaCalculator.haversineKm(b, a), within(0.001));
    }

    @Test
    void etaSecondsRemaining_zeroProgress_fullDistanceAtSpeed() {
        // 60 km at 60 km/h = 1 hour = 3600 seconds
        int eta = EtaCalculator.etaSecondsRemaining(60.0, 0.0, 60.0);
        assertThat(eta).isEqualTo(3600);
    }

    @Test
    void etaSecondsRemaining_halfProgress_halfTime() {
        // 100 km at 100 km/h, 50% done = 50 km remaining = 1800 seconds
        int eta = EtaCalculator.etaSecondsRemaining(100.0, 50.0, 100.0);
        assertThat(eta).isEqualTo(1800);
    }

    @Test
    void etaSecondsRemaining_fullProgress_returnsZero() {
        int eta = EtaCalculator.etaSecondsRemaining(100.0, 100.0, 60.0);
        assertThat(eta).isEqualTo(0);
    }

    @Test
    void etaSecondsRemaining_zeroSpeed_returnsZero() {
        int eta = EtaCalculator.etaSecondsRemaining(100.0, 0.0, 0.0);
        assertThat(eta).isEqualTo(0);
    }

    @Test
    void etaSecondsRemaining_zeroDistance_returnsZero() {
        int eta = EtaCalculator.etaSecondsRemaining(0.0, 0.0, 60.0);
        assertThat(eta).isEqualTo(0);
    }

    @Test
    void etaSecondsRemaining_fractionalResult_isCeilingRounded() {
        int eta = EtaCalculator.etaSecondsRemaining(1.0, 0.0, 7200.0);
        assertThat(eta).isEqualTo(1);
    }
}
