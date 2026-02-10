package org.example.kafkacapstoneproject.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@Configuration
@EnableScheduling
@EnableKafkaStreams
@ConfigurationProperties(prefix = "spring.kafka")
public class AppConfig {

    public static final String GITHUB_ACCOUNTS_TOPIC = "github-accounts";
    public static final String GITHUB_COMMITS_TOPIC = "github-commits";
    public static final String GITHUB_METRICS_TOPIC = "github-metrics";

    @Bean
    public NewTopic accountTopic() {
        return TopicBuilder.name(GITHUB_ACCOUNTS_TOPIC)
                .partitions(3)
                .replicas(2)
                .build();
    }

    @Bean
    public NewTopic metricsTopic() {
        return TopicBuilder.name(GITHUB_METRICS_TOPIC)
                .partitions(3)
                .replicas(2)
                .build();
    }

    @Bean
    public NewTopic commitsTopic() {
        return TopicBuilder.name(GITHUB_COMMITS_TOPIC)
                .partitions(3)
                .replicas(2)
                .build();
    }

    @Bean
    public GitHub gitHubClient() throws IOException {
        return new GitHubBuilder()
                .withEndpoint("https://api.github.com")
                .build();
    }
}
