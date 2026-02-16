package org.example.kafkacapstoneproject.github;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.kafkacapstoneproject.model.GitHubAccountMessage;
import org.example.kafkacapstoneproject.model.GithubCommitMessage;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHCommitSearchBuilder;
import org.kohsuke.github.GHDirection;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHRepositorySearchBuilder;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedIterator;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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


        try {
            List<GHRepository> repos = new ArrayList<>();
            PagedIterator<GHCommit> itCommits = search.list().iterator();
            log.info("List received");
            while (itCommits.hasNext()) {
                GHCommit commit = itCommits.next();
                repos.add(commit.getOwner());
            }
            log.info("Repos collected");

            final GHRepositorySearchBuilder searchRepo = github.searchRepositories();
            repos.forEach(repository -> searchRepo.repo(repository.getName()));

            Map<String, String> repoToLanguage = searchRepo.list().toSet().stream().collect(Collectors.toMap(GHRepository::getName, GHRepository::getLanguage));
            log.info("Map of repos constructed");
            itCommits = search.list().iterator();
            while (itCommits.hasNext()) {
                GHCommit commit = itCommits.next();
                results.add(new GithubCommitMessage(commit, repoToLanguage.get(commit.getOwner().getName())));
            }
        } catch (IOException e) {
            log.error("Error during search", e);
            throw new RuntimeException(e);
        }

        log.info("Commits: {}", results);
        return results;
    }
}
