package com.itau.banking.transaction.shared.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String BACEN_NOTIFICATIONS_TOPIC = "bacen-notifications";
    private static final String RETENTION_MS_CONFIG = "604800000"; // 7 Dias
    private static final String COMPRESSION_TYPE = "snappy";
    private static final String MIN_REPLICATIONS = "1";
    private static final int PARTITIONS = 3;
    private static final int REPLICATIONS = 3;

    @Bean
    public NewTopic bacenNotificationsTopic() {
        return TopicBuilder.name(BACEN_NOTIFICATIONS_TOPIC)
                .partitions(PARTITIONS)
                .replicas(REPLICATIONS)
                .config("retention.ms", RETENTION_MS_CONFIG)
                .config("compression.type", COMPRESSION_TYPE)
                .config("min.insync.replicas", MIN_REPLICATIONS)
                .build();
    }
}
