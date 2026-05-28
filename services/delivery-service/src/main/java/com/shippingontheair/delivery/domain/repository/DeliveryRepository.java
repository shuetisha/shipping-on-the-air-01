package com.shippingontheair.delivery.domain.repository;

import com.shippingontheair.delivery.domain.model.Delivery;
import com.shippingontheair.delivery.domain.model.DeliveryStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryRepository {

    Delivery save(Delivery delivery);

    Optional<Delivery> findById(UUID id);

    List<Delivery> findByStatus(DeliveryStatus status);
}
