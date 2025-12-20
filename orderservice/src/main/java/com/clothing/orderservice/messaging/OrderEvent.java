package com.clothing.orderservice.messaging;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderEvent {

    private UUID orderId;
    private UUID userId;
    private BigDecimal totalAmount;
    private String status;

    // Required for deserialization
    public OrderEvent() {}

    public OrderEvent(UUID orderId, UUID userId, BigDecimal totalAmount, String status) {
        this.orderId = orderId;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
