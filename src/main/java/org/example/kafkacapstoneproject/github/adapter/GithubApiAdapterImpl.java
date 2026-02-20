package org.example.kafkacapstoneproject.github.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.kafkacapstoneproject.model.GitHubAccountMessage;
import org.example.kafkacapstoneproject.model.GithubCommitMessage;
import org.kohsuke.github.GHCommitSearchBuilder;
import org.kohsuke.github.GHDirection;
import org.kohsuke.github.GitHub;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class GithubApiAdapterImpl implements GithubApiAdapter {

    private final GitHub github;

    @Override
    public Flux<GithubCommitMessage> getCommits(GitHubAccountMessage requestParams) {
        log.info("Getting commits for {}", requestParams.getAccountName());

        GHCommitSearchBuilder search = github.searchCommits()
                .author(requestParams.getAccountName())
                .committerDate(">=" + requestParams.getDate())
                .sort(GHCommitSearchBuilder.Sort.AUTHOR_DATE)
                .order(GHDirection.DESC);

        log.info("Search for {}, {}", requestParams.getAccountName(), requestParams.getDate());

        return Flux.fromIterable(search.list())
                .map(commit -> {
                    try {
                        log.info("Transforming search result for commit {}", commit);
                        return new GithubCommitMessage(commit, commit.getOwner() != null ? commit.getOwner().getLanguage() : null);
                    } catch (IOException e) {
                        log.error("Error during commit processing", e);
                        throw new RuntimeException(e);
                    }
                })
                .doOnNext(commit -> log.info("Commit constructed {}", commit))
                .doOnError(e -> log.error("Error during search", e))
                .doOnComplete(() -> log.info("Commit search completed"));
    }
}
