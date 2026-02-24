package org.example.kafkacapstoneproject.integration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.kafkacapstoneproject.github.adapter.GithubApiAdapter;
import org.example.kafkacapstoneproject.model.GitHubAccountMessage;
import org.example.kafkacapstoneproject.model.GithubCommitMessage;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * Mock implementation of GithubApiAdapter for testing purposes.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Profile("test")
@Primary
public class MockGithubApiAdapter implements GithubApiAdapter {

    @Override
    public Flux<GithubCommitMessage> getCommits(GitHubAccountMessage requestParams) {
        log.info("MockGithubApiAdapter.getCommits() called with requestParams: {}", requestParams);
        GithubCommitMessage commit1 = GithubCommitMessage.random();
        commit1.setCommitterName(requestParams.getAccountName());
        commit1.setAuthorName(requestParams.getAccountName());
        GithubCommitMessage commit2 = GithubCommitMessage.random();
        commit2.setCommitterName(requestParams.getAccountName());
        return Flux.just(commit1, commit2);
    }
}
