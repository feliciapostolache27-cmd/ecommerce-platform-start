package com.notification.event;

import java.time.Instant;

/**
 * Copia locala a contractului de mesaj (JSON) trimis de backend pe topicul "task-created".
 * Serviciile NU partajeaza cod: se inteleg doar prin forma JSON-ului.
 */
public record TaskCreatedEvent(
        Long taskId,
        Long userId,
        String userEmail,
        String title,
        Instant createdAt
) {
}
