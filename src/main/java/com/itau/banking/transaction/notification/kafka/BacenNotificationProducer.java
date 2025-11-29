package com.itau.banking.transaction.notification.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itau.banking.transaction.notification.dto.BacenKafkaMessage;
import com.itau.banking.transaction.shared.config.KafkaTopicConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class BacenNotificationProducer {
    
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    public void sendNotification(BacenKafkaMessage message) {
        try {
            String messageJson = objectMapper.writeValueAsString(message);
            String key = message.getSourceAccountNumber();
            
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(
                    KafkaTopicConfig.BACEN_NOTIFICATIONS_TOPIC, key, messageJson);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("[Kafka Producer] Message sent successfully to topic: {} - Transaction: {} - Partition: {} - Offset: {}",
                            KafkaTopicConfig.BACEN_NOTIFICATIONS_TOPIC,
                            message.getTransactionId(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                } else {
                    log.error("[Kafka Producer] Failed to send message to topic: {} - Transaction: {} - Error: {}",
                            KafkaTopicConfig.BACEN_NOTIFICATIONS_TOPIC,
                            message.getTransactionId(),
                            ex.getMessage());
                }
            });
            
        } catch (Exception e) {
            log.error("[Kafka Producer] Error serializing message for transaction: {} - Error: {}",
                    message.getTransactionId(),
                    e.getMessage());
        }
    }
}
