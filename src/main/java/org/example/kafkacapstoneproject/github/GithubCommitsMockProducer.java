package org.example.kafkacapstoneproject.github;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.kafkacapstoneproject.config.AppConfig;
import org.example.kafkacapstoneproject.model.GithubCommitMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GithubCommitsMockProducer {

    private final KafkaTemplate<String, GithubCommitMessage> kafkaTemplate;

    @Scheduled(fixedRate = 1000)
    public void send() {
        log.info("Sending commit message");
        kafkaTemplate.send(AppConfig.GITHUB_COMMITS_TOPIC, GithubCommitMessage.random());
    }
}
