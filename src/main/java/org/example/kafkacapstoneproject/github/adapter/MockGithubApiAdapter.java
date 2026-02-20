package org.example.kafkacapstoneproject.github.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.kafkacapstoneproject.model.GitHubAccountMessage;
import org.example.kafkacapstoneproject.model.GithubCommitMessage;
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
public class MockGithubApiAdapter implements GithubApiAdapter {

    @Override
    public Flux<GithubCommitMessage> getCommits(GitHubAccountMessage requestParams) {
        log.info("MockGithubApiAdapter.getCommits() called with requestParams: {}", requestParams);
        return Flux.just(GithubCommitMessage.random(), GithubCommitMessage.random(), GithubCommitMessage.random());
    }
}
