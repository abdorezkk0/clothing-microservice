package com.clothing.cartservice.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.clothing.cartservice.model.CartItem;

public interface CartRepository extends JpaRepository<CartItem, UUID> {

    List<CartItem> findByUserId(UUID userId);

    Optional<CartItem> findByUserIdAndProductId(UUID userId, UUID productId);

    @Modifying  // ✅ ADDED
    void deleteByUserId(UUID userId);

    @Modifying  // ✅ ADDED
    void deleteByUserIdAndProductId(UUID userId, UUID productId);

    default BigDecimal calculateCartTotal(UUID userId) {
        return findByUserId(userId).stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    default int calculateTotalQuantity(UUID userId) {
        return findByUserId(userId).stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
}