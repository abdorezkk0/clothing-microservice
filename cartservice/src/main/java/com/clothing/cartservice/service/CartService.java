package com.clothing.cartservice.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.clothing.cartservice.model.CartItem;
import com.clothing.cartservice.repository.CartRepository;

@Service
public class CartService {

    private final CartRepository cartItemRepository;

    public CartService(CartRepository cartItemRepository) {
        this.cartItemRepository = cartItemRepository;
    }

    public CartItem addItem(UUID userId, UUID productId, int quantity, BigDecimal price) {
        CartItem item = cartItemRepository
                .findByUserIdAndProductId(userId, productId)
                .orElse(new CartItem(userId, productId, 0, price));

        item.setQuantity(item.getQuantity() + quantity);
        item.calculateSubtotal();

        return cartItemRepository.save(item);
    }

    public List<CartItem> getCart(UUID userId) {
        return cartItemRepository.findByUserId(userId);
    }

    public CartItem updateQuantity(UUID userId, UUID productId, int quantity) {
        CartItem item = cartItemRepository
                .findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        item.setQuantity(quantity);
        item.calculateSubtotal();
        return cartItemRepository.save(item);
    }

    public void removeItem(UUID userId, UUID productId) {
        cartItemRepository.deleteByUserIdAndProductId(userId, productId);
    }

    public void clearCart(UUID userId) {
        cartItemRepository.deleteByUserId(userId);
    }

    public BigDecimal getCartTotal(UUID userId) {
        return cartItemRepository.calculateCartTotal(userId);
    }

    public int getTotalQuantity(UUID userId) {
        return cartItemRepository.calculateTotalQuantity(userId);
    }
}
