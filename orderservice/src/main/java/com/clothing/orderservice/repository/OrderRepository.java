package com.clothing.orderservice.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clothing.orderservice.model.Order;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findByUserIdOrderByOrderDateDesc(UUID userId);

    List<Order> findByStatus(String status);
}
