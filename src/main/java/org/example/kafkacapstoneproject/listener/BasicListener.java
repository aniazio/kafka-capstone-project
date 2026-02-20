package org.example.kafkacapstoneproject.listener;

import lombok.extern.slf4j.Slf4j;
import org.example.kafkacapstoneproject.config.AppConfig;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Basic listener for github metrics for debugging purposes.
 */
@Component
@Slf4j
@Profile("test")
public class BasicListener {

    @KafkaListener(id = "basic-listener", topics = AppConfig.GITHUB_METRICS_TOPIC)
    public void listen(String message) {
        log.info("Received message: {}", message);
    }
}
