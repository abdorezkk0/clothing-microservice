package com.clothing.orderservice.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.clothing.orderservice.dto.CartItemDTO;
import com.clothing.orderservice.dto.CheckoutRequest;
import com.clothing.orderservice.model.Order;
import com.clothing.orderservice.model.OrderItem;
import com.clothing.orderservice.repository.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order checkout(CheckoutRequest request) {
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setShippingAddress(request.getShippingAddress());

        for (CartItemDTO itemDTO : request.getItems()) {
            OrderItem item = new OrderItem();
            item.setProductId(itemDTO.getProductId());
            item.setQuantity(itemDTO.getQuantity());
            item.setPrice(itemDTO.getPrice());
            item.calculateSubtotal();

            order.addItem(item);
        }

        return orderRepository.save(order);
    }

    public Order getOrder(UUID id) {
        return orderRepository.findById(id).orElseThrow();
    }
}
