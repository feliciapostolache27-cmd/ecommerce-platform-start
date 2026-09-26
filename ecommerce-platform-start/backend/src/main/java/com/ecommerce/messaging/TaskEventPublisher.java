package com.ecommerce.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Trimite evenimentul pe Kafka DOAR dupa ce tranzactia (salvarea task-ului in PostgreSQL) s-a incheiat cu succes.
 * Daca Kafka nu e disponibil, task-ul ramane creat: eroarea este doar logata, nu strica raspunsul catre client.
 */
@Component
public class TaskEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(TaskEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public TaskEventPublisher(KafkaTemplate<String, Object> kafkaTemplate,
                              @Value("${app.kafka.topics.task-created}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTaskCreated(TaskCreatedEvent event) {
        try {
            // cheia = userId: toate evenimentele unui user ajung in aceeasi partitie, deci pastreaza ordinea
            kafkaTemplate.send(topic, String.valueOf(event.userId()), event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Eveniment netrimis pe topicul '{}' (task {}): {}", topic, event.taskId(), ex.getMessage());
                        } else {
                            log.info("Eveniment trimis pe topicul '{}' (task {})", topic, event.taskId());
                        }
                    });
        } catch (Exception ex) {
            log.error("Kafka indisponibil, evenimentul pentru task {} nu a fost trimis: {}", event.taskId(), ex.getMessage());
        }
    }
}
