package org.example.kafkacapstoneproject.github;

import org.example.kafkacapstoneproject.model.GitHubAccountMessage;
import org.example.kafkacapstoneproject.model.GithubCommitMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHCommitSearchBuilder;
import org.kohsuke.github.GHDirection;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedSearchIterable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.List;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GithubApiAdapterTest {

    @Mock
    private GitHub github;

    @Mock
    private GHCommitSearchBuilder searchBuilder;

    @Mock
    private GHCommit commit1, commit2, commit3;

    @Mock
    private GHRepository repo;

    @Mock
    private PagedSearchIterable<GHCommit> commits;

    @InjectMocks
    private GithubApiAdapter githubApiAdapter;

    @Test
    void testGetCommitsWhenNoProblems() throws IOException {
        GitHubAccountMessage requestParams = GitHubAccountMessage.builder()
                .accountName("test")
                .date("2022-01-01").build();

        List<GHCommit> commitsList = List.of(commit1, commit2);

        when(github.searchCommits()).thenReturn(searchBuilder);
        when(searchBuilder.author(requestParams.getAccountName())).thenReturn(searchBuilder);
        when(searchBuilder.committerDate(">=2022-01-01")).thenReturn(searchBuilder);
        when(searchBuilder.sort(GHCommitSearchBuilder.Sort.AUTHOR_DATE)).thenReturn(searchBuilder);
        when(searchBuilder.order(GHDirection.DESC)).thenReturn(searchBuilder);
        when(searchBuilder.list()).thenReturn(commits);
        when(commits.spliterator()).thenReturn(commitsList.spliterator());

        when(commit1.getSHA1()).thenReturn("commit1");
        when(commit2.getSHA1()).thenReturn("commit2");
        when(commit2.getOwner()).thenReturn(repo);
        when(repo.getLanguage()).thenReturn("Java");

        Flux<GithubCommitMessage> result = githubApiAdapter.getCommits(requestParams);

        StepVerifier.create(result)
                .expectNextMatches(commit -> commit.getCommitId().equals("commit1"))
                .expectNextMatches(commit -> commit.getCommitId().equals("commit2")
                        && commit.getLanguage().equals("Java"))
                .verifyComplete();
        then(github).should().searchCommits();
        then(searchBuilder).should().author(requestParams.getAccountName());
        then(searchBuilder).should().committerDate(">=2022-01-01");
        then(searchBuilder).should().sort(GHCommitSearchBuilder.Sort.AUTHOR_DATE);
        then(searchBuilder).should().order(GHDirection.DESC);
        then(searchBuilder).should().list();
        then(commits).should().spliterator();
        then(commit1).should().getSHA1();
        then(commit2).should().getSHA1();
        then(commit1).should().getAuthor();
        then(commit2).should().getAuthor();
    }

    @Test
    void testGetCommitsWhenException() throws IOException {
        GitHubAccountMessage requestParams = GitHubAccountMessage.builder()
                .accountName("test")
                .date("2022-01-01").build();

        List<GHCommit> commitsList = List.of(commit1, commit2, commit3);

        when(github.searchCommits()).thenReturn(searchBuilder);
        when(searchBuilder.author(requestParams.getAccountName())).thenReturn(searchBuilder);
        when(searchBuilder.committerDate(">=2022-01-01")).thenReturn(searchBuilder);
        when(searchBuilder.sort(GHCommitSearchBuilder.Sort.AUTHOR_DATE)).thenReturn(searchBuilder);
        when(searchBuilder.order(GHDirection.DESC)).thenReturn(searchBuilder);
        when(searchBuilder.list()).thenReturn(commits);
        when(commits.spliterator()).thenReturn(commitsList.spliterator());

        when(commit1.getSHA1()).thenReturn("commit1");
        when(commit2.getSHA1()).thenReturn("commit2");
        when(commit2.getAuthor()).thenThrow(new IOException("test"));

        Flux<GithubCommitMessage> result = githubApiAdapter.getCommits(requestParams);

        StepVerifier.create(result)
                .expectNextMatches(commit -> commit.getCommitId().equals("commit1"))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException)
                .verify();
    }
}
