package org.example.kafkacapstoneproject.github.adapter;

import org.example.kafkacapstoneproject.model.GitHubAccountMessage;
import org.example.kafkacapstoneproject.model.GithubCommitMessage;
import reactor.core.publisher.Flux;

public interface GithubApiAdapter {

    Flux<GithubCommitMessage> getCommits(GitHubAccountMessage requestParams);
}
