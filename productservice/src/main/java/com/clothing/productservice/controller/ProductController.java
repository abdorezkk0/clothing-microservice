package com.clothing.productservice.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clothing.productservice.model.Product;
import com.clothing.productservice.service.ProductService;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createProduct(@RequestBody Product product) {
        try {
            Product createdProduct = productService.createProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Product created successfully",
                "productId", createdProduct.getId(),
                "name", createdProduct.getName()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(productService.getProductsByCategory(category));
    }

    @GetMapping("/brand/{brand}")
    public ResponseEntity<List<Product>> getProductsByBrand(@PathVariable String brand) {
        return ResponseEntity.ok(productService.getProductsByBrand(brand));
    }

    @GetMapping("/available")
    public ResponseEntity<List<Product>> getAvailableProducts() {
        return ResponseEntity.ok(productService.getAvailableProducts());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String name) {
        return ResponseEntity.ok(productService.searchProductsByName(name));
    }

    @GetMapping("/category/{category}/available")
    public ResponseEntity<List<Product>> getAvailableProductsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(productService.getAvailableProductsByCategory(category));
    }

    @GetMapping("/brand/{brand}/available")
    public ResponseEntity<List<Product>> getAvailableProductsByBrand(@PathVariable String brand) {
        return ResponseEntity.ok(productService.getAvailableProductsByBrand(brand));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        try {
            Product updatedProduct = productService.updateProduct(id, product);
            return ResponseEntity.ok(Map.of(
                "message", "Product updated successfully",
                "productId", updatedProduct.getId(),
                "name", updatedProduct.getName()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<Map<String, Object>> updateStock(
            @PathVariable Long id, 
            @RequestBody Map<String, Integer> stockUpdate) {
        try {
            Product product = productService.updateStock(id, stockUpdate.get("quantity"));
            return ResponseEntity.ok(Map.of(
                "message", "Stock updated successfully",
                "productId", product.getId(),
                "newStock", product.getStockQuantity(),
                "available", product.isAvailable()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/{id}/decrease-stock")
    public ResponseEntity<Map<String, Object>> decreaseStock(
            @PathVariable Long id, 
            @RequestBody Map<String, Integer> stockUpdate) {
        try {
            Product product = productService.decreaseStock(id, stockUpdate.get("quantity"));
            return ResponseEntity.ok(Map.of(
                "message", "Stock decreased successfully",
                "productId", product.getId(),
                "remainingStock", product.getStockQuantity()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/{id}/increase-stock")
    public ResponseEntity<Map<String, Object>> increaseStock(
            @PathVariable Long id, 
            @RequestBody Map<String, Integer> stockUpdate) {
        try {
            Product product = productService.increaseStock(id, stockUpdate.get("quantity"));
            return ResponseEntity.ok(Map.of(
                "message", "Stock increased successfully",
                "productId", product.getId(),
                "newStock", product.getStockQuantity()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/{id}/check-availability")
    public ResponseEntity<Map<String, Object>> checkAvailability(
            @PathVariable Long id, 
            @RequestParam Integer quantity) {
        try {
            boolean available = productService.checkAvailability(id, quantity);
            return ResponseEntity.ok(Map.of(
                "productId", id,
                "requestedQuantity", quantity,
                "available", available
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(Map.of(
                "message", "Product deleted successfully"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }
}