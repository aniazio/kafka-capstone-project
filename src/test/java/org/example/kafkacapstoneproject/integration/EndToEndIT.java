package org.example.kafkacapstoneproject.integration;

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
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;

@EmbeddedKafka(partitions = 1, bootstrapServersProperty = "${spring.embedded.kafka.brokers}")
@TestPropertySource(properties = {
        "spring.kafka.streams.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"
})
@SpringBootTest
@ActiveProfiles("test")
public class EndToEndIT {

    @Value("${spring.embedded.kafka.brokers}")
    private String BOOTSTRAP_SERVERS;

    @Autowired
    private StreamsBuilderFactoryBean streamsBuilderFactoryBean;

    @Test
    void testEndToEnd() {
        Map<String, Object> configs = KafkaTestUtils.consumerProps(BOOTSTRAP_SERVERS, "test-group", true);

        Consumer<String, MetricsAggregation> consumer = new DefaultKafkaConsumerFactory<>(configs, new StringDeserializer(), CustomSerdes.metricsAggregation().deserializer()).createConsumer();
        consumer.subscribe(List.of(AppConfig.GITHUB_METRICS_TOPIC));

        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofMillis(500))
                .ignoreExceptions()
                .untilAsserted(() -> {
                    ConsumerRecords<String, MetricsAggregation> records = consumer.poll(Duration.ofMillis(200));
                    assertFalse(records.isEmpty());
                });
        consumer.close();
    }
}
