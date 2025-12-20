package com.clothing.productservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clothing.productservice.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(String category);
    List<Product> findByBrand(String brand);
    List<Product> findByAvailable(boolean available);
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByCategoryAndAvailable(String category, boolean available);
    List<Product> findByBrandAndAvailable(String brand, boolean available);
}