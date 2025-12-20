package com.clothing.productservice.messaging;

import org.springframework.stereotype.Component;

@Component
public class ProductEventPublisher {

    // Uncomment when RabbitMQ/Kafka is configured
    /*
    private final RabbitTemplate rabbitTemplate;

    public ProductEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishProductCreated(Product product) {
        Map<String, Object> event = Map.of(
            "eventType", "PRODUCT_CREATED",
            "productId", product.getId(),
            "name", product.getName(),
            "price", product.getPrice(),
            "stockQuantity", product.getStockQuantity(),
            "timestamp", LocalDateTime.now()
        );
        
        rabbitTemplate.convertAndSend("product-exchange", "product.created", event);
        System.out.println("📤 Published PRODUCT_CREATED event: " + product.getName());
    }

    public void publishStockUpdated(Product product) {
        Map<String, Object> event = Map.of(
            "eventType", "STOCK_UPDATED",
            "productId", product.getId(),
            "stockQuantity", product.getStockQuantity(),
            "available", product.isAvailable(),
            "timestamp", LocalDateTime.now()
        );
        
        rabbitTemplate.convertAndSend("product-exchange", "product.stock.updated", event);
        System.out.println("📤 Published STOCK_UPDATED event for product: " + product.getId());
    }
    */

    // Kafka example
    /*
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void publishProductEvent(Product product, String eventType) {
        String message = String.format("{\"eventType\":\"%s\",\"productId\":%d,\"name\":\"%s\"}",
            eventType, product.getId(), product.getName());
        
        kafkaTemplate.send("product-events", message);
        System.out.println("📤 Published to Kafka: " + eventType);
    }
    */
}