package com.clothing.orderservice.dto;

import java.util.List;
import java.util.UUID;

public class CheckoutRequest {

    private UUID userId;
    private String shippingAddress;
    private List<CartItemDTO> items;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public List<CartItemDTO> getItems() {
        return items;
    }

    public void setItems(List<CartItemDTO> items) {
        this.items = items;
    }
}
