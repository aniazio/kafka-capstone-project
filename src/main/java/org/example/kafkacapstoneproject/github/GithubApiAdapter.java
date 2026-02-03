package org.example.kafkacapstoneproject.github;

import org.example.kafkacapstoneproject.model.GitHubAccountMessage;
import org.example.kafkacapstoneproject.model.GitHubResponse;
import org.example.kafkacapstoneproject.model.GithubCommitMessage;
import org.example.kafkacapstoneproject.webclient.WebClientHelper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static org.springframework.http.HttpHeaders.ACCEPT;

@Service
public class GithubApiAdapter {

    private final WebClient webClient;

    public GithubApiAdapter(@Qualifier("githubWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public List<GithubCommitMessage> getCommits(GitHubAccountMessage requestParams) {
        String searchQuery = "author-name:" + requestParams.getAccountName() + " +author-date>" + requestParams.getDate();
        return webClient.get()
                .uri("search/commits?q=" + searchQuery)
                .header(ACCEPT, "application/vnd.github+json")
                .header("X-GitHub-Api-Version", "2022-11-28")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, WebClientHelper::handleError)
                .onStatus(HttpStatusCode::is5xxServerError, WebClientHelper::handleError)
                .bodyToMono(GitHubResponse.class)
                .map(GitHubResponse::getCommitMessages)
                .block();
    }
}
