package org.example.kafkacapstoneproject.listener;

import lombok.extern.slf4j.Slf4j;
import org.example.kafkacapstoneproject.metrics.MetricsProcessingStream;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BasicListener {

    @KafkaListener(id = "basic-listener", topics = {MetricsProcessingStream.TOTAL_COMMITS, MetricsProcessingStream.TOTAL_COMMITERS, MetricsProcessingStream.COMMITS_PER_LANGUAGE, MetricsProcessingStream.TOP_FIVE_CONTRIBUTORS_BY_COMMITS, MetricsProcessingStream.NUMBER_OF_LINES_EDITED, MetricsProcessingStream.INCREMENT_OF_LINES, MetricsProcessingStream.TOP_FIVE_CONTRIBUTORS_BY_LINES_EDITED, MetricsProcessingStream.PERCENT_OF_COMMITS_WITH_THE_SAME_AUTHOR_AND_COMMITER})
    public void listen(String message) {
        log.info("Received message: {}", message);
    }
}
