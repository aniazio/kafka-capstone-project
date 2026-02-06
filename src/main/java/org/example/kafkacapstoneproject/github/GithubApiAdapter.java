package org.example.kafkacapstoneproject.github;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.kafkacapstoneproject.model.GitHubAccountMessage;
import org.example.kafkacapstoneproject.model.GithubCommitMessage;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
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
        List<GithubCommitMessage> results = new ArrayList<>();
        GHUser user;
        try {
            user = github.getUser(requestParams.getAccountName());

            for (GHRepository repo : user.listRepositories()) {
                String language = repo.getLanguage(); // Primary language

                List<GithubCommitMessage> commits = repo.queryCommits()
                        .author(requestParams.getAccountName())
                        .since(requestParams.getDate())
                        .list()
                        .toList()
                        .stream()
                        .map(commit -> {
                            try {
                                return new GithubCommitMessage(commit, language);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .toList();

                results.addAll(commits);
            }
        } catch (IOException e) {
            log.error("Error while getting commits", e);
        }
        return results;
    }
}
