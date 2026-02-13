package org.example.kafkacapstoneproject.github;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.kafkacapstoneproject.model.GitHubAccountMessage;
import org.example.kafkacapstoneproject.model.GithubCommitMessage;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHCommitSearchBuilder;
import org.kohsuke.github.GHDirection;
import org.kohsuke.github.GitHub;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GithubApiAdapter {

    private final GitHub github;

    public List<GithubCommitMessage> getCommits(GitHubAccountMessage requestParams) {
        log.info("Getting commits for {}", requestParams.getAccountName());
        List<GithubCommitMessage> results = new ArrayList<>();

        GHCommitSearchBuilder search = github.searchCommits()
                .author(requestParams.getAccountName())
                .committerDate(requestParams.getDate())
                .sort(GHCommitSearchBuilder.Sort.AUTHOR_DATE)
                .order(GHDirection.DESC);

        log.info("Search for {}, {}", requestParams.getAccountName(), requestParams.getDate());

        for (GHCommit commit : search.list()) {
            try {
                results.add(new GithubCommitMessage(commit, commit.getOwner().getLanguage()));
            } catch (IOException e) {
                log.error("Error while constructing commit message", e);
            }
        }

        log.info("Commits: {}", results);
        return results;
    }
}
