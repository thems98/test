package se.liu.ida.tdp024.account.logic.util;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    public static final String TOPIC_TRANSACTIONS  = "transactions";
    public static final String TOPIC_REST_REQUESTS = "rest-requests";

    @Bean
    public NewTopic transactionsTopic() {
        return TopicBuilder.name(TOPIC_TRANSACTIONS)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic restRequestsTopic() {
        return TopicBuilder.name(TOPIC_REST_REQUESTS)
                .partitions(1)
                .replicas(1)
                .build();
    }
}