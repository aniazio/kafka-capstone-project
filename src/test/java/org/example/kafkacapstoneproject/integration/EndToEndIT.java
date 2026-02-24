package org.example.kafkacapstoneproject.integration;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.awaitility.Awaitility;
import org.example.kafkacapstoneproject.config.AppConfig;
import org.example.kafkacapstoneproject.model.MetricsAggregation;
import org.example.kafkacapstoneproject.serdes.CustomSerdes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EmbeddedKafka(partitions = 1, bootstrapServersProperty = "${spring.embedded.kafka.brokers}",
        topics = {"github-commits", "github-metrics", "github-accounts"})
@TestPropertySource(properties = {
        "spring.kafka.streams.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"
})
@SpringBootTest
@ActiveProfiles("test")
@Slf4j
public class EndToEndIT {

    @Value("${spring.embedded.kafka.brokers}")
    private String BOOTSTRAP_SERVERS;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void testEndToEnd() {
        Map<String, Object> consumerConfigs = KafkaTestUtils.consumerProps(BOOTSTRAP_SERVERS, "test-group", true);
        Consumer<String, MetricsAggregation> consumer = new DefaultKafkaConsumerFactory<>(consumerConfigs, new StringDeserializer(), CustomSerdes.metricsAggregation().deserializer()).createConsumer();
        consumer.subscribe(List.of(AppConfig.GITHUB_METRICS_TOPIC));

        sendMessages();

        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofMillis(500))
                .ignoreExceptions()
                .untilAsserted(() -> {
                    ConsumerRecords<String, MetricsAggregation> records = consumer.poll(Duration.ofMillis(1000));
                    assertFalse(records.isEmpty());
                    records.forEach(record -> {
                        log.info("Received record: {}", record.value());
                        assertNotNull(record.value());
                        assertTrue(record.value().getTotalCommits() > 4);
                        String nameOfTopContributor = record.value().getTopFiveContributorsByLines().getNamesByCommits().pollLast().getRight();
                        assertEquals("TestUser", nameOfTopContributor);
                        nameOfTopContributor = record.value().getTopFiveContributorsByCommits().getNamesByCommits().pollLast().getRight();
                        assertEquals("TestUser", nameOfTopContributor);
                        assertEquals(50.0, record.value().getPercentOfCommitsWithTheSameAuthorAndCommitter());
                    });
                });
        consumer.close();
    }

    private void sendMessages() {
        Flux.interval(Duration.ofSeconds(1))
                .take(10)
                .doOnNext(i -> kafkaTemplate.send(AppConfig.GITHUB_ACCOUNTS_TOPIC, "TestUser,1y"))
                .subscribe();
    }
}
