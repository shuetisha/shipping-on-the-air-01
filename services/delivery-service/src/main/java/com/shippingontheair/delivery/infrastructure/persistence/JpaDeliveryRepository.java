package com.shippingontheair.delivery.infrastructure.persistence;

import com.shippingontheair.delivery.domain.model.Delivery;
import com.shippingontheair.delivery.domain.model.DeliveryStatus;
import com.shippingontheair.delivery.domain.repository.DeliveryRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class JpaDeliveryRepository implements DeliveryRepository {

    private final DeliveryJpaRepository jpaRepository;

    public JpaDeliveryRepository(DeliveryJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Delivery save(Delivery delivery) {
        return DeliveryMapper.toDomain(jpaRepository.save(DeliveryMapper.toEntity(delivery)));
    }

    @Override
    public Optional<Delivery> findById(UUID id) {
        return jpaRepository.findById(id).map(DeliveryMapper::toDomain);
    }

    @Override
    public List<Delivery> findByStatus(DeliveryStatus status) {
        return jpaRepository.findByStatus(status.name()).stream().map(DeliveryMapper::toDomain).toList();
    }
}
