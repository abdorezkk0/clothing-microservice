package com.clothing.cartservice.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clothing.cartservice.model.CartItem;
import com.clothing.cartservice.service.CartService;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public ResponseEntity<CartItem> addToCart(
            @RequestParam UUID userId,
            @RequestParam UUID productId,
            @RequestParam int quantity,
            @RequestParam BigDecimal price) {

        return ResponseEntity.ok(
                cartService.addItem(userId, productId, quantity, price)
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CartItem>> getCart(@PathVariable UUID userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PutMapping("/update")
    public ResponseEntity<CartItem> updateItem(
            @RequestParam UUID userId,
            @RequestParam UUID productId,
            @RequestParam int quantity) {

        return ResponseEntity.ok(
                cartService.updateQuantity(userId, productId, quantity)
        );
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeItem(
            @RequestParam UUID userId,
            @RequestParam UUID productId) {

        cartService.removeItem(userId, productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable UUID userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary/{userId}")
    public ResponseEntity<Map<String, Object>> getSummary(@PathVariable UUID userId) {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalAmount", cartService.getCartTotal(userId));
        summary.put("totalItems", cartService.getTotalQuantity(userId));
        return ResponseEntity.ok(summary);
    }
}
