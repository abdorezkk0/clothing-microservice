package com.clothing.orderservice.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clothing.orderservice.dto.CheckoutRequest;
import com.clothing.orderservice.dto.UpdateStatusRequest;
import com.clothing.orderservice.model.Order;
import com.clothing.orderservice.model.OrderItem;
import com.clothing.orderservice.service.OrderService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // 1. Create Order (Checkout)
    @PostMapping("/checkout")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<Order> checkout(@RequestBody CheckoutRequest request) {
        Order order = orderService.checkout(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    // 2. Get Order by ID
    @GetMapping("/{id}")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public ResponseEntity<Order> getOrder(@PathVariable String id) {
        Order order = orderService.getOrder(id);
        return ResponseEntity.ok(order);
    }

    // 3. Get All Orders for a User - Smart endpoint (handles both list and paginated)
    @GetMapping("/user/{userId}")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public ResponseEntity<?> getOrdersByUserId(
            @PathVariable String userId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        
        // If page and size are provided, return paginated results
        if (page != null && size != null) {
            Sort.Direction direction = sortDirection.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            Page<Order> orders = orderService.getOrdersByUserId(userId, pageable);
            return ResponseEntity.ok(orders);
        }
        
        // Otherwise, return simple list
        List<Order> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    // 5. Get All Orders (Admin) - Smart endpoint
    @GetMapping
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public ResponseEntity<?> getAllOrders(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        
        // If page and size are provided, return paginated results
        if (page != null && size != null) {
            Sort.Direction direction = sortDirection.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            Page<Order> orders = orderService.getAllOrders(pageable);
            return ResponseEntity.ok(orders);
        }
        
        // Otherwise return all orders with a reasonable limit
        Page<Order> orders = orderService.getAllOrders(PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC, sortBy)));
        return ResponseEntity.ok(orders.getContent());
    }

    // 6. Get Orders by Status - Smart endpoint
    @GetMapping("/status/{status}")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public ResponseEntity<?> getOrdersByStatus(
            @PathVariable String status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        
        // If page and size are provided, return paginated results
        if (page != null && size != null) {
            Sort.Direction direction = sortDirection.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            Page<Order> orders = orderService.getOrdersByStatus(status, pageable);
            return ResponseEntity.ok(orders);
        }
        
        // Otherwise, return simple list
        List<Order> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }

    // 8. Update Order Status
    @PutMapping("/{id}/status")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable String id,
            @RequestBody UpdateStatusRequest request) {
        Order order = orderService.updateOrderStatus(id, request.getStatus());
        return ResponseEntity.ok(order);
    }

    // 9. Cancel Order
    @PutMapping("/{id}/cancel")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<Map<String, String>> cancelOrder(@PathVariable String id) {
        orderService.cancelOrder(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Order cancelled successfully");
        response.put("orderId", id);
        response.put("status", "CANCELLED");
        return ResponseEntity.ok(response);
    }

    // 10. Delete Order (Hard Delete)
    @DeleteMapping("/{id}")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<Map<String, String>> deleteOrder(@PathVariable String id) {
        orderService.deleteOrder(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Order deleted successfully");
        response.put("orderId", id);
        return ResponseEntity.ok(response);
    }

    // 11. Get Order Items
    @GetMapping("/{id}/items")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public ResponseEntity<List<OrderItem>> getOrderItems(@PathVariable String id) {
        Order order = orderService.getOrder(id);
        return ResponseEntity.ok(order.getItems());
    }
}