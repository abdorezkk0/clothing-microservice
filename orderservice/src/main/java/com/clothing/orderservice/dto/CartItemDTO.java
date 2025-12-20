package com.clothing.orderservice.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class CartItemDTO {

    private UUID productId;
    private int quantity;
    private BigDecimal price;

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
