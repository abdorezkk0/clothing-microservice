package com.clothing.productservice.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clothing.productservice.model.Product;
import com.clothing.productservice.repository.ProductRepository;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(Product product) {
        System.out.println("🛍️ Creating new product: " + product.getName());
        System.out.println("   Category: " + product.getCategory());
        System.out.println("   Price: $" + product.getPrice());
        System.out.println("   Stock: " + product.getStockQuantity());
        
        return productRepository.save(product);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public List<Product> getProductsByBrand(String brand) {
        return productRepository.findByBrand(brand);
    }

    public List<Product> getAvailableProducts() {
        return productRepository.findByAvailable(true);
    }

    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Product> getAvailableProductsByCategory(String category) {
        return productRepository.findByCategoryAndAvailable(category, true);
    }

    public List<Product> getAvailableProductsByBrand(String brand) {
        return productRepository.findByBrandAndAvailable(brand, true);
    }

    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setStockQuantity(productDetails.getStockQuantity());
        product.setCategory(productDetails.getCategory());
        product.setBrand(productDetails.getBrand());
        product.setSize(productDetails.getSize());
        product.setColor(productDetails.getColor());
        product.setImageUrl(productDetails.getImageUrl());
        product.setAvailable(productDetails.isAvailable());

        System.out.println("📝 Updating product: " + product.getName());
        
        return productRepository.save(product);
    }

    public Product updateStock(Long id, Integer quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        product.setStockQuantity(quantity);
        
        if (quantity <= 0) {
            product.setAvailable(false);
            System.out.println("⚠️ Product out of stock: " + product.getName());
        } else {
            product.setAvailable(true);
            System.out.println("✅ Stock updated for: " + product.getName() + " - New quantity: " + quantity);
        }

        return productRepository.save(product);
    }

    public Product decreaseStock(Long id, Integer quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock for product: " + product.getName());
        }

        int newQuantity = product.getStockQuantity() - quantity;
        return updateStock(id, newQuantity);
    }

    public Product increaseStock(Long id, Integer quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        int newQuantity = product.getStockQuantity() + quantity;
        return updateStock(id, newQuantity);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        System.out.println("🗑️ Deleting product: " + product.getName());
        productRepository.deleteById(id);
    }

    public boolean checkAvailability(Long id, Integer quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        return product.isAvailable() && product.getStockQuantity() >= quantity;
    }
}