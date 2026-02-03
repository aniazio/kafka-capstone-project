package org.example.kafkacapstoneproject.webclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

@Slf4j
public class WebClientHelper {

    private WebClientHelper() {
    }

    public static Mono<? extends Throwable> handleError(ClientResponse clientResponse) {
        log.error(clientResponse.toString());
        return Mono.error(new RuntimeException("Failed to trigger file reader"));
    }
}
