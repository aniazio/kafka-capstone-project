package org.example.kafkacapstoneproject.sender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaBasicSender {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topicName = "test-topic";
    private int counter = 1;

    @Scheduled(fixedDelay = 2, timeUnit = TimeUnit.SECONDS)
    public void sendMessage() {
        log.info("Sending message: " + counter);
        var future = kafkaTemplate.send(topicName, "Message " + counter++);
        future.whenComplete((result, exception) -> {
            if(exception != null) {
                log.error("Error while sending message", exception);
            } else {
                log.info("Message sent successfully");
            }
        }
        );
    }

}
