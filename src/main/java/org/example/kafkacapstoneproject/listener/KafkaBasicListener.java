package org.example.kafkacapstoneproject.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaBasicListener {

    @KafkaListener(id="basic-listener", topics = "test-topic")
    public void listen(String message) {
        log.info("Received message: " + message);
    }
}
