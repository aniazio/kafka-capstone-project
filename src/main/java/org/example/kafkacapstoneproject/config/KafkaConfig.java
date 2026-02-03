package org.example.kafkacapstoneproject.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableScheduling
public class KafkaConfig {

    public static final String GITHUB_ACCOUNTS_TOPIC = "github-accounts";

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name(GITHUB_ACCOUNTS_TOPIC)
                .partitions(3)
                .replicas(2)
                .build();
    }

    @Bean("githubWebClient")
    public WebClient githubWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.github.com")
                .build();
    }

    @Bean("fileReaderConnectorWebClient")
    public WebClient fileReaderConnectorWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8083")
                .build();
    }

}
