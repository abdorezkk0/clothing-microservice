package com.clothing.cartservice.model;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "cart_items",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "product_id"}))
public class CartItem {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    private int quantity;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(precision = 10, scale = 2)
    private BigDecimal subtotal;

    public CartItem() {}

    public CartItem(UUID userId, UUID productId, int quantity, BigDecimal price) {
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        calculateSubtotal();
    }

    public void calculateSubtotal() {
        this.subtotal = price.multiply(BigDecimal.valueOf(quantity));
    }

    public UUID getId() {
    return id;
}

public void setId(UUID id) {
    this.id = id;
}

public UUID getUserId() {
    return userId;
}

public void setUserId(UUID userId) {
    this.userId = userId;
}

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
    calculateSubtotal();
}

public BigDecimal getPrice() {
    return price;
}

public void setPrice(BigDecimal price) {
    this.price = price;
    calculateSubtotal();
}

public BigDecimal getSubtotal() {
    return subtotal;
}

public void setSubtotal(BigDecimal subtotal) {
    this.subtotal = subtotal;
}

}
