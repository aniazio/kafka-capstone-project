package org.example.kafkacapstoneproject.listener;

import lombok.extern.slf4j.Slf4j;
import org.example.kafkacapstoneproject.config.AppConfig;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BasicListener {

    @KafkaListener(id = "basic-listener", topics = AppConfig.GITHUB_METRICS_TOPIC)
    public void listen(String message) {
        log.info("Received message: {}", message);
    }
}
