package org.example.kafkacapstoneproject.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
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

    private final WebClient webClientReader = WebClient.builder()
            .baseUrl("http://localhost:8083")
            .build();
    private final WebClient webClientWriter = WebClient.builder()
            .baseUrl("http://localhost:8084")
            .build();
    private final Map<String, Object> payloadReader;
    private final Map<String, Object> payloadWriter;

    public ConnectorTrigger() {
        payloadReader = Map.of(
                "name", "csv-loader",
                "config", Map.of(
                        "connector.class", "org.apache.kafka.connect.file.FileStreamSourceConnector",
                        "file", "/data/github-accounts.csv",
                        "topic", AppConfig.GITHUB_ACCOUNTS_TOPIC
                )
        );
        payloadWriter = Map.of(
                "name", "metrics-sink",
                "config", Map.of(
                        "connector.class", "org.apache.kafka.connect.file.FileStreamSinkConnector",
                        "file", "/output/metrics.csv",
                        "topic", AppConfig.GITHUB_METRICS_TOPIC
                )
        );
    }

    @PostConstruct
    public void triggerFileReader() {
        trigger(webClientReader, payloadReader);
        trigger(webClientWriter, payloadWriter);
    }

    private void trigger(WebClient webClientReader, Map<String, Object> payloadReader) {
        webClientReader.post()
                .uri("/connectors")
                .bodyValue(payloadReader)
                .header(CONTENT_TYPE, "application/json")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, this::handleError)
                .onStatus(HttpStatusCode::is5xxServerError, this::handleError)
                .bodyToMono(Void.class)
                .doOnError((Throwable e) -> log.error("Error triggering file reader connector", e))
                .block();
    }

    public Mono<? extends Throwable> handleError(ClientResponse clientResponse) {
        log.error(clientResponse.toString());
        return Mono.empty();
    }
}
