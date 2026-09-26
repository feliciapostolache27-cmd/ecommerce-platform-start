package com.notification.listener;

import com.notification.event.TaskCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumatorul Kafka. Nu trimitem emailuri reale: "notificarea" este doar afisata in log.
 * Grupul "notification-service" face ca, la mai multe instante, fiecare mesaj sa fie procesat o singura data.
 */
@Component
public class TaskEventListener {

    private static final Logger log = LoggerFactory.getLogger(TaskEventListener.class);

    @KafkaListener(topics = "${app.kafka.topics.task-created}", groupId = "notification-service")
    public void onTaskCreated(TaskCreatedEvent event) {
        log.info("[NOTIFICARE] Task nou #{} \"{}\" creat de {} (userId={}) la {}",
                event.taskId(), event.title(), event.userEmail(), event.userId(), event.createdAt());
    }
}
