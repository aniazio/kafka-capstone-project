package org.example.kafkacapstoneproject.github;

import org.example.kafkacapstoneproject.model.GithubCommitMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import reactor.core.publisher.Flux;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GithubCommitsProducerTest {

    @Mock
    private KafkaTemplate<String, GithubCommitMessage> kafkaTemplate;

    @Mock
    private GithubApiAdapter githubApiAdapter;

    @InjectMocks
    private GithubCommitsProducer githubCommitsProducer;

    @Mock
    private GithubCommitMessage commit1, commit2, commit3;

    @Test
    void testListenWhenNoProblems() {
        String properMessage = "account,1h";
        when(githubApiAdapter.getCommits(assertArg(message -> "account".equals(message.getAccountName()))))
                .thenReturn(Flux.just(commit1, commit2));
        when(kafkaTemplate.send("github-commits", "account", commit1)).thenReturn(CompletableFuture.completedFuture(null));
        when(kafkaTemplate.send("github-commits", "account", commit2)).thenReturn(CompletableFuture.completedFuture(null));

        githubCommitsProducer.listen(properMessage);

        then(kafkaTemplate).should(times(2)).send(anyString(), anyString(), any());
    }

    @Test
    void testListenWhenErrorOnSendShouldProceed() {
        String properMessage = "account,1h";
        when(githubApiAdapter.getCommits(assertArg(message -> "account".equals(message.getAccountName()))))
                .thenReturn(Flux.just(commit1, commit2, commit3));
        when(kafkaTemplate.send("github-commits", "account", commit1)).thenReturn(CompletableFuture.completedFuture(null));
        when(kafkaTemplate.send("github-commits", "account", commit2)).thenReturn(CompletableFuture.failedFuture(new RuntimeException("test")));
        when(kafkaTemplate.send("github-commits", "account", commit3)).thenReturn(CompletableFuture.completedFuture(null));

        githubCommitsProducer.listen(properMessage);

        then(kafkaTemplate).should(times(3)).send(anyString(), anyString(), any());
    }

    @Test
    void testListenWhenEmptyStringExpectNoMessages() {
        String properMessage = "";

        githubCommitsProducer.listen(properMessage);

        then(kafkaTemplate).shouldHaveNoInteractions();
        then(githubApiAdapter).shouldHaveNoInteractions();
    }
}
