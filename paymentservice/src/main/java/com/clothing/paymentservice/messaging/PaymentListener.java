package com.clothing.paymentservice.messaging;

import java.math.BigDecimal;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.clothing.paymentservice.dto.PaymentEventDTO;
import com.clothing.paymentservice.model.Payment;
import com.clothing.paymentservice.service.PaymentService;

@Component
public class PaymentListener {

    private static final Logger logger = LoggerFactory.getLogger(PaymentListener.class);
    
    private final PaymentService paymentService;
    private final PaymentEventPublisher paymentEventPublisher;

    public PaymentListener(PaymentService paymentService, PaymentEventPublisher paymentEventPublisher) {
        this.paymentService = paymentService;
        this.paymentEventPublisher = paymentEventPublisher;
    }

    /**
     * Listen for payment requests from order service
     * Queue: payment-queue (or order.payment.queue)
     */
    @RabbitListener(queues = "${rabbitmq.queue.payment.request:payment-request-queue}")
    public void handlePaymentRequest(Map<String, Object> message) {
        logger.info("📨 Received payment request: {}", message);
        
        try {
            // Extract payment details from message
            Payment payment = new Payment();
            payment.setOrderId(Long.parseLong(message.get("orderId").toString()));
            payment.setUserId(Long.parseLong(message.get("userId").toString()));
            payment.setAmount(new BigDecimal(message.get("amount").toString()));
            
            // Parse payment method
            String paymentMethodStr = message.get("paymentMethod").toString();
            Payment.PaymentMethod paymentMethod = Payment.PaymentMethod.valueOf(paymentMethodStr.toUpperCase());
            payment.setPaymentMethod(paymentMethod);
            
            logger.info("Creating payment for Order ID: {}, Amount: {}", 
                       payment.getOrderId(), payment.getAmount());
            
            // Create payment
            Payment createdPayment = paymentService.createPayment(payment);
            logger.info("Payment created with ID: {}", createdPayment.getId());
            
            // Process payment
            Payment processedPayment = paymentService.processPayment(createdPayment.getId());
            logger.info("Payment processed successfully. Status: {}", processedPayment.getStatus());
            
            // Publish success event
            PaymentEventDTO successEvent = new PaymentEventDTO(
                processedPayment.getId(),
                processedPayment.getOrderId(),
                processedPayment.getUserId(),
                processedPayment.getAmount(),
                processedPayment.getStatus().toString(),
                processedPayment.getPaymentMethod().toString(),
                processedPayment.getTransactionId(),
                "Payment processed successfully"
            );
            paymentEventPublisher.publishPaymentSuccess(successEvent);
            
        } catch (Exception e) {
            logger.error("❌ Error processing payment request: {}", e.getMessage(), e);
            
            // Publish failure event
            try {
                Long orderId = Long.parseLong(message.get("orderId").toString());
                Long userId = Long.parseLong(message.get("userId").toString());
                BigDecimal amount = new BigDecimal(message.get("amount").toString());
                
                PaymentEventDTO failureEvent = new PaymentEventDTO(
                    null,
                    orderId,
                    userId,
                    amount,
                    "FAILED",
                    message.get("paymentMethod").toString(),
                    null,
                    "Payment processing failed: " + e.getMessage()
                );
                paymentEventPublisher.publishPaymentFailed(failureEvent);
            } catch (Exception publishError) {
                logger.error("Failed to publish failure event: {}", publishError.getMessage());
            }
        }
    }

    /**
     * Listen for payment success confirmations
     * This is for receiving confirmations from other services
     */
    @RabbitListener(queues = "${rabbitmq.queue.payment.success}")
    public void handlePaymentSuccess(PaymentEventDTO event) {
        logger.info("✅ Payment success event received: {}", event);
        // Additional processing if needed (e.g., update analytics, send notifications)
    }

    /**
     * Listen for payment failure notifications
     */
    @RabbitListener(queues = "${rabbitmq.queue.payment.failed}")
    public void handlePaymentFailure(PaymentEventDTO event) {
        logger.warn("❌ Payment failure event received: {}", event);
        // Additional processing if needed (e.g., retry logic, send alerts)
    }

    /**
     * Listen for payment refund requests
     */
    @RabbitListener(queues = "${rabbitmq.queue.payment.refund}")
    public void handlePaymentRefund(PaymentEventDTO event) {
        logger.info("🔄 Payment refund event received: {}", event);
        
        try {
            if (event.getPaymentId() != null) {
                Payment refundedPayment = paymentService.refundPayment(event.getPaymentId());
                logger.info("Payment refunded successfully. ID: {}", refundedPayment.getId());
                
                // Publish refund confirmation
                PaymentEventDTO refundConfirmation = new PaymentEventDTO(
                    refundedPayment.getId(),
                    refundedPayment.getOrderId(),
                    refundedPayment.getUserId(),
                    refundedPayment.getAmount(),
                    refundedPayment.getStatus().toString(),
                    refundedPayment.getPaymentMethod().toString(),
                    refundedPayment.getTransactionId(),
                    "Payment refunded successfully"
                );
                paymentEventPublisher.publishPaymentRefund(refundConfirmation);
            }
        } catch (Exception e) {
            logger.error("❌ Error processing refund: {}", e.getMessage(), e);
        }
    }
}