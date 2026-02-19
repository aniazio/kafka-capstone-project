package org.example.kafkacapstoneproject.github;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.kafkacapstoneproject.config.AppConfig;
import org.example.kafkacapstoneproject.model.GitHubAccountMessage;
import org.example.kafkacapstoneproject.model.GithubCommitMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GithubCommitsProducer {

    private final KafkaTemplate<String, GithubCommitMessage> kafkaTemplate;
    private final GithubApiAdapter githubApiAdapter;

    @KafkaListener(id = "github-listener", topics = AppConfig.GITHUB_ACCOUNTS_TOPIC)
    public void listen(String message) {
        log.info("Github account message received: {}", message);
        final GitHubAccountMessage gitHubAccountMessage = GitHubAccountMessage.buildFromCsv(message);
        if (gitHubAccountMessage == null) {
            log.error("Invalid message: {}", message);
            return;
        }
        githubApiAdapter.getCommits(gitHubAccountMessage)
                .doOnNext(commit -> sendCommit(commit, gitHubAccountMessage.getAccountName()))
                .doOnNext(commit -> log.info("Commit received {}", commit))
                .subscribe();
    }

    private void sendCommit(GithubCommitMessage commit, String accountName) {
        kafkaTemplate.send(AppConfig.GITHUB_COMMITS_TOPIC, accountName, commit)
                .whenComplete(this::handleException);
    }

    private void handleException(SendResult<String, GithubCommitMessage> stringGithubCommitMessageSendResult, Throwable throwable) {
        if (throwable != null) {
            log.error("Error while sending message", throwable);
        } else {
            log.info("Message {} sent successfully", stringGithubCommitMessageSendResult);
        }
    }
}
