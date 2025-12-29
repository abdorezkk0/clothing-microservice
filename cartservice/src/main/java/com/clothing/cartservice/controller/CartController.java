package com.clothing.cartservice.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clothing.cartservice.model.CartItem;
import com.clothing.cartservice.service.CartService;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {

    private final CartService cartService;
    
    // ✅ TEMPORARY: Map username to userId (simulates UserService lookup)
    private static final Map<String, UUID> userIdCache = new ConcurrentHashMap<>();

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<?> getMyCart() {
        try {
            String username = getCurrentUsername();
            UUID userId = getUserId(username);
            
            System.out.println("🛒 [CartController] Getting cart for user: " + username + " (ID: " + userId + ")");
            
            List<CartItem> items = cartService.getCart(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("username", username);
            response.put("userId", userId);
            response.put("items", items);
            response.put("totalAmount", cartService.getCartTotal(userId));
            response.put("totalItems", cartService.getTotalQuantity(userId));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ [CartController] Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody AddToCartRequest request) {
        try {
            String username = getCurrentUsername();
            UUID userId = getUserId(username);
            
            System.out.println("🛒 [CartController] Adding to cart for: " + username + " (ID: " + userId + ")");
            System.out.println("🛒 Product: " + request.getProductId() + ", Qty: " + request.getQuantity());
            
            CartItem item = cartService.addItem(
                userId, 
                request.getProductId(), 
                request.getQuantity(), 
                request.getPrice()
            );
            
            System.out.println("✅ [CartController] Item added: " + item.getId());
            return ResponseEntity.ok(item);
            
        } catch (Exception e) {
            System.err.println("❌ [CartController] Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateItemQuantity(@RequestBody UpdateCartRequest request) {
        try {
            String username = getCurrentUsername();
            UUID userId = getUserId(username);
            
            System.out.println("🛒 [CartController] Updating cart for: " + username + " (ID: " + userId + ")");
            System.out.println("🛒 Product: " + request.getProductId() + ", New Qty: " + request.getQuantity());
            
            CartItem item = cartService.updateQuantity(userId, request.getProductId(), request.getQuantity());
            
            System.out.println("✅ [CartController] Item updated: " + item.getId());
            return ResponseEntity.ok(item);
            
        } catch (Exception e) {
            System.err.println("❌ [CartController] Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeItem(@RequestBody RemoveCartRequest request) {
        try {
            String username = getCurrentUsername();
            UUID userId = getUserId(username);
            
            System.out.println("🛒 [CartController] Removing item for: " + username + " (ID: " + userId + ")");
            System.out.println("🛒 Product: " + request.getProductId());
            
            cartService.removeItem(userId, request.getProductId());
            
            System.out.println("✅ [CartController] Item removed");
            return ResponseEntity.ok(Map.of("message", "Item removed successfully"));
            
        } catch (Exception e) {
            System.err.println("❌ [CartController] Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearMyCart() {
        try {
            String username = getCurrentUsername();
            UUID userId = getUserId(username);
            
            System.out.println("🛒 [CartController] Clearing cart for: " + username + " (ID: " + userId + ")");
            
            cartService.clearCart(userId);
            
            System.out.println("✅ [CartController] Cart cleared");
            return ResponseEntity.ok(Map.of("message", "Cart cleared successfully"));
            
        } catch (Exception e) {
            System.err.println("❌ [CartController] Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<?> getMySummary() {
        try {
            String username = getCurrentUsername();
            UUID userId = getUserId(username);
            
            System.out.println("🛒 [CartController] Getting summary for: " + username + " (ID: " + userId + ")");
            
            Map<String, Object> summary = new HashMap<>();
            summary.put("username", username);
            summary.put("userId", userId);
            summary.put("totalAmount", cartService.getCartTotal(userId));
            summary.put("totalItems", cartService.getTotalQuantity(userId));
            
            return ResponseEntity.ok(summary);
            
        } catch (Exception e) {
            System.err.println("❌ [CartController] Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // Legacy endpoints
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CartItem>> getCart(@PathVariable UUID userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
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

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }
        return authentication.getName();
    }
    
    // ✅ Get or create userId for username (simulates UserService)
    private UUID getUserId(String username) {
        return userIdCache.computeIfAbsent(username, k -> {
            UUID newId = UUID.randomUUID();
            System.out.println("🆔 [CartController] Created new userId for " + username + ": " + newId);
            return newId;
        });
    }
}

// DTOs
class AddToCartRequest {
    private Long productId;
    private int quantity;
    private BigDecimal price;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}

class UpdateCartRequest {
    private Long productId;
    private int quantity;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}

class RemoveCartRequest {
    private Long productId;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
}