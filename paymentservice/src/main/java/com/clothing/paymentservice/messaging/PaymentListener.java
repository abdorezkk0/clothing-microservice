package com.clothing.paymentservice.messaging;

import org.springframework.stereotype.Component;

import com.clothing.paymentservice.service.PaymentService;

@Component
public class PaymentListener {

    private final PaymentService paymentService;

    public PaymentListener(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // Uncomment when RabbitMQ/Kafka is configured
    /*
    @RabbitListener(queues = "payment-queue")
    public void handlePaymentRequest(Map<String, Object> message) {
        System.out.println("📨 Received payment request: " + message);
        
        Payment payment = new Payment();
        payment.setOrderId(Long.parseLong(message.get("orderId").toString()));
        payment.setUserId(Long.parseLong(message.get("userId").toString()));
        payment.setAmount(new BigDecimal(message.get("amount").toString()));
        payment.setPaymentMethod(Payment.PaymentMethod.valueOf(message.get("paymentMethod").toString()));
        
        Payment createdPayment = paymentService.createPayment(payment);
        paymentService.processPayment(createdPayment.getId());
    }
    */

    // Example for Kafka
    /*
    @KafkaListener(topics = "payment-requests", groupId = "payment-service")
    public void handlePaymentEvent(String message) {
        System.out.println("📨 Received payment event: " + message);
        // Process payment event
    }
    */
}