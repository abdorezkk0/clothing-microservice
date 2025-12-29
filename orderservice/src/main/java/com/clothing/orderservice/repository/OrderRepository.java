package com.clothing.orderservice.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clothing.orderservice.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    List<Order> findByUserIdOrderByOrderDateDesc(String userId);

    List<Order> findByStatus(String status);
    
    // Find all orders by user ID
    List<Order> findByUserId(String userId);
    
    // Find orders by user ID and status
    List<Order> findByUserIdAndStatus(String userId, String status);
    
    // Paginated version - find all orders
    Page<Order> findAll(Pageable pageable);
    
    // Paginated version - find by user ID
    Page<Order> findByUserId(String userId, Pageable pageable);
    
    // Paginated version - find by status
    Page<Order> findByStatus(String status, Pageable pageable);
}