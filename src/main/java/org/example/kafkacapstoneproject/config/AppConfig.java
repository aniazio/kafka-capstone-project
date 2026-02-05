package org.example.kafkacapstoneproject.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.example.kafkacapstoneproject.model.GithubCommitMessage;
import org.kohsuke.github.GitHub;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableScheduling
@EnableKafkaStreams
@ConfigurationProperties(prefix = "spring.kafka")
public class AppConfig {

    public static final String GITHUB_ACCOUNTS_TOPIC = "github-accounts";
    public static final String GITHUB_METRICS_PREFIX = "github-metrics-";
    private String bootstrapServers;

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

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kafkaStreamConfiguration() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-metrics");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.serdeFrom(GithubCommitMessage.class).getClass().getName());

        return new KafkaStreamsConfiguration(props);
    }
}
