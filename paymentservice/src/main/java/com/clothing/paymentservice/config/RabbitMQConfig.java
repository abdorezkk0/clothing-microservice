package com.clothing.paymentservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.payment}")
    private String paymentExchange;

    @Value("${rabbitmq.queue.payment.request}")
    private String paymentRequestQueue;

    @Value("${rabbitmq.queue.payment.success}")
    private String paymentSuccessQueue;

    @Value("${rabbitmq.queue.payment.failed}")
    private String paymentFailedQueue;

    @Value("${rabbitmq.queue.payment.refund}")
    private String paymentRefundQueue;

    @Value("${rabbitmq.routing.key.payment.request}")
    private String paymentRequestRoutingKey;

    @Value("${rabbitmq.routing.key.payment.success}")
    private String paymentSuccessRoutingKey;

    @Value("${rabbitmq.routing.key.payment.failed}")
    private String paymentFailedRoutingKey;

    @Value("${rabbitmq.routing.key.payment.refund}")
    private String paymentRefundRoutingKey;

    // Exchange
    @Bean
    public TopicExchange paymentExchange() {
        return new TopicExchange(paymentExchange);
    }

    // Queues
    @Bean
    public Queue paymentRequestQueue() {
        return new Queue(paymentRequestQueue, true);
    }

    @Bean
    public Queue paymentSuccessQueue() {
        return new Queue(paymentSuccessQueue, true);
    }

    @Bean
    public Queue paymentFailedQueue() {
        return new Queue(paymentFailedQueue, true);
    }

    @Bean
    public Queue paymentRefundQueue() {
        return new Queue(paymentRefundQueue, true);
    }

    // Bindings
    @Bean
    public Binding paymentRequestBinding() {
        return BindingBuilder
                .bind(paymentRequestQueue())
                .to(paymentExchange())
                .with(paymentRequestRoutingKey);
    }

    @Bean
    public Binding paymentSuccessBinding() {
        return BindingBuilder
                .bind(paymentSuccessQueue())
                .to(paymentExchange())
                .with(paymentSuccessRoutingKey);
    }

    @Bean
    public Binding paymentFailedBinding() {
        return BindingBuilder
                .bind(paymentFailedQueue())
                .to(paymentExchange())
                .with(paymentFailedRoutingKey);
    }

    @Bean
    public Binding paymentRefundBinding() {
        return BindingBuilder
                .bind(paymentRefundQueue())
                .to(paymentExchange())
                .with(paymentRefundRoutingKey);
    }

    // Message Converter
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // RabbitTemplate
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}