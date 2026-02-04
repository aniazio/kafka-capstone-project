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

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GithubCommitsProducer {

    private final KafkaTemplate<String, GithubCommitMessage> kafkaTemplate;
    private final GithubApiAdapter githubApiAdapter;

    @KafkaListener(id = "github-listener", topics = AppConfig.GITHUB_ACCOUNTS_TOPIC)
    public void listen(String message) {
        GitHubAccountMessage gitHubAccountMessage = GitHubAccountMessage.buildFromCsv(message);
        if (gitHubAccountMessage == null) {
            return;
        }
        List<GithubCommitMessage> commits = githubApiAdapter.getCommits(gitHubAccountMessage);
        sendCommits(commits);
    }

    private void sendCommits(List<GithubCommitMessage> commits) {
        commits.stream()
                .map(commit -> kafkaTemplate.send(AppConfig.GITHUB_ACCOUNTS_TOPIC, commit))
                .forEach(future -> future.whenComplete(this::handleException));
    }

    private void handleException(SendResult<String, GithubCommitMessage> stringGithubCommitMessageSendResult, Throwable throwable) {
        if (throwable != null) {
            log.error("Error while sending message", throwable);
        } else {
            log.info("Message {} sent successfully", stringGithubCommitMessageSendResult);
        }
    }
}
