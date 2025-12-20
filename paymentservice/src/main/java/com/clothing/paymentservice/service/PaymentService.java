package com.clothing.paymentservice.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clothing.paymentservice.model.Payment;
import com.clothing.paymentservice.model.Payment.PaymentStatus;
import com.clothing.paymentservice.repository.PaymentRepository;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment createPayment(Payment payment) {
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTransactionId(generateTransactionId());
        
        System.out.println("💳 Creating payment:");
        System.out.println("   Order ID: " + payment.getOrderId());
        System.out.println("   Amount: $" + payment.getAmount());
        System.out.println("   Method: " + payment.getPaymentMethod());
        
        return paymentRepository.save(payment);
    }

    public Payment processPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new RuntimeException("Payment cannot be processed. Current status: " + payment.getStatus());
        }

        payment.setStatus(PaymentStatus.PROCESSING);
        paymentRepository.save(payment);

        System.out.println("⚙️ Processing payment ID: " + paymentId);

        // Simulate payment gateway processing
        boolean paymentSuccess = simulatePaymentGateway(payment);

        if (paymentSuccess) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setPaymentGatewayResponse("Payment processed successfully");
            System.out.println("✅ Payment completed: " + payment.getTransactionId());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setPaymentGatewayResponse("Payment gateway declined the transaction");
            System.err.println("❌ Payment failed: " + payment.getTransactionId());
        }

        return paymentRepository.save(payment);
    }

    public Payment refundPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new RuntimeException("Only completed payments can be refunded");
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setPaymentGatewayResponse("Payment refunded");
        
        System.out.println("💰 Refunding payment: " + payment.getTransactionId());
        
        return paymentRepository.save(payment);
    }

    public Payment cancelPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new RuntimeException("Only pending payments can be cancelled");
        }

        payment.setStatus(PaymentStatus.CANCELLED);
        payment.setPaymentGatewayResponse("Payment cancelled by user");
        
        System.out.println("🚫 Cancelling payment: " + payment.getTransactionId());
        
        return paymentRepository.save(payment);
    }

    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

    public List<Payment> getPaymentsByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    public List<Payment> getPaymentsByUserId(Long userId) {
        return paymentRepository.findByUserId(userId);
    }

    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }

    public Optional<Payment> getPaymentByTransactionId(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    // Helper methods
    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private boolean simulatePaymentGateway(Payment payment) {
        // Simulate payment processing (90% success rate)
        try {
            Thread.sleep(1000); // Simulate network delay
            return Math.random() > 0.1; // 90% success rate
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}