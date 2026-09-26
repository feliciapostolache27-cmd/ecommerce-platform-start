package com.ecommerce.messaging;

import java.time.Instant;

/**
 * Evenimentul "s-a creat un task nou". Este trimis prin Kafka catre Notification Service.
 * Este un simplu obiect de date (JSON pe topic), nu o entitate.
 */
public record TaskCreatedEvent(
        Long taskId,
        Long userId,
        String userEmail,
        String title,
        Instant createdAt
) {
}
