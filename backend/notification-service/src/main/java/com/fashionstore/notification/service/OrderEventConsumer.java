package com.fashionstore.notification.service;

import com.fashionstore.notification.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Listens on Kafka topic "order-events" and reacts to OrderCreatedEvent by
 * sending a confirmation email (mục 4.6 và mục 10 trong tài liệu).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final EmailService emailService;

    @KafkaListener(topics = "order-events", groupId = "notification-service-group")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Received OrderCreatedEvent from Kafka: {}", event);

        if (event.getUserEmail() != null && !event.getUserEmail().isBlank()) {
            emailService.sendOrderConfirmation(
                    event.getUserEmail(),
                    event.getOrderId(),
                    event.getReceiverName(),
                    event.getTotalAmount() != null ? event.getTotalAmount().toString() : "0",
                    event.getBonusPromoCode(),
                    event.getBonusPromoDiscountPercent(),
                    event.getBonusPromoDescription()
            );
        }

        // Additional channels (SMS, push notification, in-app notification) can be added here.
        log.info("Notification processed for order #{}", event.getOrderId());
    }
}
