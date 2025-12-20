package com.clothing.orderservice.model;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID productId;

    private int quantity;

    private BigDecimal price;

    private BigDecimal subtotal;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    public void calculateSubtotal() {
        this.subtotal = price.multiply(BigDecimal.valueOf(quantity));
    }

    public UUID getId() {
    return id;
}

public void setId(UUID id) {
    this.id = id;
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

public Order getOrder() {
    return order;
}

public void setOrder(Order order) {
    this.order = order;
}

}
