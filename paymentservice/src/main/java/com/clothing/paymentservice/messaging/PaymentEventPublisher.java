package com.clothing.paymentservice.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.clothing.paymentservice.dto.PaymentEventDTO;

@Service
public class PaymentEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(PaymentEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.payment}")
    private String paymentExchange;

    @Value("${rabbitmq.routing.key.payment.success}")
    private String paymentSuccessRoutingKey;

    @Value("${rabbitmq.routing.key.payment.failed}")
    private String paymentFailedRoutingKey;

    @Value("${rabbitmq.routing.key.payment.refund}")
    private String paymentRefundRoutingKey;

    public PaymentEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishPaymentSuccess(PaymentEventDTO event) {
        logger.info("Publishing payment success event: {}", event);
        rabbitTemplate.convertAndSend(paymentExchange, paymentSuccessRoutingKey, event);
        logger.info("Payment success event published successfully");
    }

    public void publishPaymentFailed(PaymentEventDTO event) {
        logger.info("Publishing payment failed event: {}", event);
        rabbitTemplate.convertAndSend(paymentExchange, paymentFailedRoutingKey, event);
        logger.info("Payment failed event published successfully");
    }

    public void publishPaymentRefund(PaymentEventDTO event) {
        logger.info("Publishing payment refund event: {}", event);
        rabbitTemplate.convertAndSend(paymentExchange, paymentRefundRoutingKey, event);
        logger.info("Payment refund event published successfully");
    }
}