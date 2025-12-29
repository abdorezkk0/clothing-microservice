package com.clothing.cartservice.service;

import com.clothing.cartservice.model.CartItem;
import com.clothing.cartservice.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class CartService {

    private final CartRepository cartRepository;

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public CartItem addItem(UUID userId, Long productId, int quantity, BigDecimal price) {
        // Check if item already exists
        CartItem existingItem = cartRepository
                .findByUserIdAndProductId(userId, productId)
                .orElse(null);

        if (existingItem != null) {
            // Update quantity
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            return cartRepository.save(existingItem);
        }

        // Create new item
        CartItem item = new CartItem();
        item.setUserId(userId);
        item.setProductId(productId);
        item.setQuantity(quantity);
        item.setPrice(price);

        return cartRepository.save(item);
    }

    public List<CartItem> getCart(UUID userId) {
        return cartRepository.findByUserId(userId);
    }

    public CartItem updateQuantity(UUID userId, Long productId, int quantity) {
        CartItem item = cartRepository
                .findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        item.setQuantity(quantity);
        return cartRepository.save(item);
    }

    public void removeItem(UUID userId, Long productId) {
        CartItem item = cartRepository
                .findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        cartRepository.delete(item);
    }

    public void clearCart(UUID userId) {
        List<CartItem> items = cartRepository.findByUserId(userId);
        cartRepository.deleteAll(items);
    }

    public BigDecimal getCartTotal(UUID userId) {
        return cartRepository.findByUserId(userId).stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getTotalQuantity(UUID userId) {
        return cartRepository.findByUserId(userId).stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
}