package org.example.kafkacapstoneproject.github;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.kafkacapstoneproject.config.AppConfig;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Random;

import static org.example.kafkacapstoneproject.model.GithubCommitMessage.RANDOM_AUTHORS;

/**
 * Mock producer for github commits for debugging purposes.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Profile("test")
public class GithubAccountMockProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Random random = new Random();

    @Scheduled(fixedRate = 1000)
    public void send() {
        log.info("Sending account string");
        kafkaTemplate.send(AppConfig.GITHUB_ACCOUNTS_TOPIC, RANDOM_AUTHORS.get(random.nextInt(RANDOM_AUTHORS.size())) + ",1y");
    }
}
