package org.example.kafkacapstoneproject.metrics;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.example.kafkacapstoneproject.config.AppConfig;
import org.example.kafkacapstoneproject.model.GithubCommitMessage;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MetricsProcessingStream {

    private final StreamsBuilder streamsBuilder;
    private final String COMMITS_PER_LANGUAGE = AppConfig.GITHUB_METRICS_PREFIX + "commits-per-language";
    private final String TOTAL_COMMITS = AppConfig.GITHUB_METRICS_PREFIX + "total-commits";

    public void process() {
        KStream<String, GithubCommitMessage> input = streamsBuilder.stream(AppConfig.GITHUB_ACCOUNTS_TOPIC);

        input.groupBy((key, value) -> "total-count")
                .count()
                .toStream()
                .to(TOTAL_COMMITS, Produced.with(Serdes.String(), Serdes.Long()));

        input.groupBy((key, value) -> value.getLanguage())
                .count()
                .toStream()
                .to(COMMITS_PER_LANGUAGE, Produced.with(Serdes.String(), Serdes.Long()));
    }
}
