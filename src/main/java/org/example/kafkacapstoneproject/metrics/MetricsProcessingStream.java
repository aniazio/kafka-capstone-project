package org.example.kafkacapstoneproject.metrics;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.example.kafkacapstoneproject.config.AppConfig;
import org.example.kafkacapstoneproject.model.GithubCommitMessage;
import org.example.kafkacapstoneproject.model.SameAuthorStats;
import org.example.kafkacapstoneproject.model.TopFiveContributors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MetricsProcessingStream {

    private final String TOTAL_COMMITS = AppConfig.GITHUB_METRICS_PREFIX + "total-commits";
    private final String TOTAL_COMMITERS = AppConfig.GITHUB_METRICS_PREFIX + "total-commiters";
    private final String COMMITS_PER_LANGUAGE = AppConfig.GITHUB_METRICS_PREFIX + "commits-per-language";
    private final String TOP_FIVE_CONTRIBUTORS_BY_COMMITS = AppConfig.GITHUB_METRICS_PREFIX + "top-five-contributors-by-commits";
    private final String NUMBER_OF_LINES_EDITED = AppConfig.GITHUB_METRICS_PREFIX + "number-of-lines-edited";
    private final String INCREMENT_OF_LINES = AppConfig.GITHUB_METRICS_PREFIX + "increment-of-lines";
    private final String TOP_FIVE_CONTRIBUTORS_BY_LINES_EDITED = AppConfig.GITHUB_METRICS_PREFIX + "top-five-contributors-by-lines-edited";
    private final String PERCENT_OF_COMMITS_WITH_THE_SAME_AUTHOR_AND_COMMITER = AppConfig.GITHUB_METRICS_PREFIX + "percent-of-commits-with-the-same-author-and-committer";

    @Autowired
    public void process(StreamsBuilder streamsBuilder) {
        KStream<String, GithubCommitMessage> input = streamsBuilder.stream(AppConfig.GITHUB_ACCOUNTS_TOPIC);

        measureTotalNumberOfCommits(input);
        measureTotalNumberOfCommiters(input);
        measureCommitsPerLanguage(input);
        measureTopFiveContributorsByCommits(input);
        measureNumberOfLinesEdited(input);
        measureIncrementOfLines(input);
        measureTopFiveContributorsByLines(input);
        measurePercentOfCommitsWithTheSameAuthorAndCommitter(input);
    }

    private void measureTotalNumberOfCommits(KStream<String, GithubCommitMessage> input) {
        input.groupBy((key, value) -> "total-count")
                .count()
                .toStream()
                .to(TOTAL_COMMITS, Produced.with(Serdes.String(), Serdes.Long()));
    }

    private void measureTotalNumberOfCommiters(KStream<String, GithubCommitMessage> input) {
        input.groupBy((key, value) -> value.getCommitterName())
                .count()
                .toStream()
                .to(TOTAL_COMMITERS, Produced.with(Serdes.String(), Serdes.Long()));
    }

    private void measureCommitsPerLanguage(KStream<String, GithubCommitMessage> input) {
        input.groupBy((key, value) -> value.getLanguage())
                .count()
                .toStream()
                .to(COMMITS_PER_LANGUAGE, Produced.with(Serdes.String(), Serdes.Long()));
    }

    private void measureTopFiveContributorsByCommits(KStream<String, GithubCommitMessage> input) {
        KTable<String, Long> commitsPerUser = input
                .groupBy((key, value) -> value.getCommitterName())
                .count();

        convertKTableToTopFive(commitsPerUser, TOP_FIVE_CONTRIBUTORS_BY_COMMITS);
    }

    private void measureNumberOfLinesEdited(KStream<String, GithubCommitMessage> input) {
        input.map((key, value) -> KeyValue.pair(key, (long) value.getLinesChanged()))
                .groupByKey()
                .reduce(Long::sum)
                .toStream()
                .to(NUMBER_OF_LINES_EDITED, Produced.with(Serdes.String(), Serdes.Long()));
    }

    private void measureIncrementOfLines(KStream<String, GithubCommitMessage> input) {
        input.map((key, value) -> KeyValue.pair(key, (long) value.getLinesAdded() - value.getLinesDeleted()))
                .groupByKey()
                .reduce(Long::sum)
                .toStream()
                .to(INCREMENT_OF_LINES, Produced.with(Serdes.String(), Serdes.Long()));
    }

    private void measureTopFiveContributorsByLines(KStream<String, GithubCommitMessage> input) {
        KTable<String, Long> linesPerUser = input
                .map((key, value) -> KeyValue.pair(value.getCommitterName(), (long) value.getLinesChanged()))
                .groupBy((key, value) -> key)
                .reduce(Long::sum);

        convertKTableToTopFive(linesPerUser, TOP_FIVE_CONTRIBUTORS_BY_LINES_EDITED);
    }

    private void measurePercentOfCommitsWithTheSameAuthorAndCommitter(KStream<String, GithubCommitMessage> input) {
        input.map((key, value) -> KeyValue.pair(value.getCommitId(), value.getAuthorName().equals(value.getCommitterName())))
                .groupBy((key, value) -> value)
                .count()
                .groupBy((key, value) -> KeyValue.pair("stats", Pair.of(key, value)),
                        Grouped.with(Serdes.String(), Serdes.serdeFrom(Pair.class)))
                .aggregate(
                        SameAuthorStats::new,
                        (key, newValue, aggregate) -> {
                            aggregate.addStat((Long) newValue.getRight(), (Boolean) newValue.getLeft());
                            return aggregate;
                        },
                        (key, oldValue, aggregate) -> {
                            aggregate.removeStat((Long) oldValue.getRight(), (Boolean) oldValue.getLeft());
                            return aggregate;
                        }
                ).toStream()
                .map((key, value) -> KeyValue.pair(key, value.getPercent()))
                .to(PERCENT_OF_COMMITS_WITH_THE_SAME_AUTHOR_AND_COMMITER, Produced.with(Serdes.String(), Serdes.Double()));
    }

    private void convertKTableToTopFive(KTable<String, Long> linesPerUser, String topicName) {
        linesPerUser
                .groupBy((key, value) -> KeyValue.pair("all", Pair.of(key, value)),
                        Grouped.with(Serdes.String(), Serdes.serdeFrom(Pair.class)))
                .aggregate(
                        TopFiveContributors::new,
                        (key, value, aggregate) -> {
                            aggregate.add((String) value.getKey(), (Long) value.getValue());
                            return aggregate;
                        },
                        (key, value, aggregate) -> {
                            aggregate.remove((String) value.getKey(), (Long) value.getValue());
                            return aggregate;
                        })
                .toStream()
                .to(topicName, Produced.with(Serdes.String(), Serdes.serdeFrom(TopFiveContributors.class)));
    }

}
