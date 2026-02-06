package org.example.kafkacapstoneproject.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

/**
 * Triggers the file reader connector to read the file and send the data to the topic.
 */
@Component
@Slf4j
public class ConnectorTrigger {

    private final WebClient webClient;
    private final Map<String, Object> payload;

    public ConnectorTrigger(@Qualifier("fileReaderConnectorWebClient") WebClient webClient) {
        this.webClient = webClient;
        payload = Map.of(
                "name", "csv-loader",
                "confing", Map.of(
                        "connector.class", "org.apache.kafka.connect.file.FileStreamSourceConnector",
                        "file", "/data/github-accounts.csv",
                        "topic", AppConfig.GITHUB_ACCOUNTS_TOPIC
                )
        );
    }

    @PostConstruct
    public void triggerFileReader() {
        webClient.post()
                .uri("/connectors")
                .bodyValue(payload)
                .header(CONTENT_TYPE, "application/json")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, this::handleError)
                .onStatus(HttpStatusCode::is5xxServerError, this::handleError)
                .bodyToMono(Void.class)
                .block();
    }

    public Mono<? extends Throwable> handleError(ClientResponse clientResponse) {
        log.error(clientResponse.toString());
        return Mono.empty();
    }
}
