package org.example.kafkacapstoneproject.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.kohsuke.github.GitHub;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

@Configuration
@EnableScheduling
public class AppConfig {

    public static final String GITHUB_ACCOUNTS_TOPIC = "github-accounts";

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name(GITHUB_ACCOUNTS_TOPIC)
                .partitions(3)
                .replicas(2)
                .build();
    }

    @Bean
    public GitHub githubWebClient() throws IOException {
        return GitHub.connect();
    }

    @Bean
    public WebClient fileReaderConnectorWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8083")
                .build();
    }

}
