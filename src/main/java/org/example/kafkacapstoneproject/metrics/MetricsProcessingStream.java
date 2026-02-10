package org.example.kafkacapstoneproject.metrics;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.example.kafkacapstoneproject.config.AppConfig;
import org.example.kafkacapstoneproject.model.GithubCommitMessage;
import org.example.kafkacapstoneproject.model.MetricsAggregation;
import org.example.kafkacapstoneproject.model.MyPairBooleanLong;
import org.example.kafkacapstoneproject.model.MyPairStringLong;
import org.example.kafkacapstoneproject.model.SameAuthorStats;
import org.example.kafkacapstoneproject.model.TopFiveContributors;
import org.example.kafkacapstoneproject.serdes.CustomSerdes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MetricsProcessingStream {

    @Autowired
    public void process(StreamsBuilder streamsBuilder) {
        KStream<String, GithubCommitMessage> input = streamsBuilder.stream(AppConfig.GITHUB_ACCOUNTS_TOPIC,
                Consumed.with(Serdes.String(), CustomSerdes.githubCommitMessage()));

        KTable<String, Long> totalCommits = measureTotalNumberOfCommits(input);
        KTable<String, Long> totalCommiters = measureTotalNumberOfCommiters(input);
        KTable<String, Map<String, Long>> commitsPerLanguage = measureCommitsPerLanguage(input);
        KTable<String, TopFiveContributors> topFiveContributorsByCommits = measureTopFiveContributorsByCommits(input);
        KTable<String, Long> numberOfLines = measureNumberOfLinesEdited(input);
        KTable<String, Long> incrementOfLines = measureIncrementOfLines(input);
        KTable<String, TopFiveContributors> topFiveContributorsByLines = measureTopFiveContributorsByLines(input);
        KTable<String, Double> percentOfCommitsWithTheSameAuthorAndCommitter = measurePercentOfCommitsWithTheSameAuthorAndCommitter(input);

        totalCommits.leftJoin(totalCommiters,
                        MetricsAggregation::new)
                .leftJoin(commitsPerLanguage,
                        (metrics, newMetric) -> {
                            metrics.setCommitsPerLanguage(newMetric);
                            return metrics;
                        })
                .leftJoin(topFiveContributorsByCommits,
                        (metrics, newMetric) -> {
                            metrics.setTopFiveContributorsByCommits(newMetric);
                            return metrics;
                        })
                .leftJoin(numberOfLines,
                        (metrics, newMetric) -> {
                            metrics.setNumberOfLines(newMetric);
                            return metrics;
                        })
                .leftJoin(incrementOfLines,
                        (metrics, newMetric) -> {
                            metrics.setIncrementOfLines(newMetric);
                            return metrics;
                        })
                .leftJoin(topFiveContributorsByLines,
                        (metrics, newMetric) -> {
                            metrics.setTopFiveContributorsByLines(newMetric);
                            return metrics;
                        })
                .leftJoin(percentOfCommitsWithTheSameAuthorAndCommitter,
                        (metrics, newMetric) -> {
                            metrics.setPercentOfCommitsWithTheSameAuthorAndCommitter(newMetric);
                            return metrics;
                        })
                .toStream()
                .to(AppConfig.GITHUB_METRICS_TOPIC, Produced.with(Serdes.String(), CustomSerdes.metricsAggregation()));
    }

    private KTable<String, Long> measureTotalNumberOfCommits(KStream<String, GithubCommitMessage> input) {
        return input.groupBy((key, value) -> "total-count")
                .count();
    }

    private KTable<String, Long> measureTotalNumberOfCommiters(KStream<String, GithubCommitMessage> input) {
        return input.groupBy((key, value) -> value.getCommitterName())
                .count();
    }

    private KTable<String, Map<String, Long>> measureCommitsPerLanguage(KStream<String, GithubCommitMessage> input) {
        return input.groupBy((key, value) -> value.getLanguage())
                .count()
                .groupBy((key, value) -> KeyValue.pair("all", MyPairStringLong.of(key, value)),
                        Grouped.with(Serdes.String(), CustomSerdes.myPairStringLong()))
                .aggregate(HashMap::new,
                        (key, value, aggregate) -> {
                            aggregate.put((String) value.getLeft(), (Long) value.getRight());
                            return aggregate;
                        },
                        (key, value, aggregate) -> {
                            aggregate.remove((String) value.getLeft(), (Long) value.getRight());
                            return aggregate;
                        });
    }

    private KTable<String, TopFiveContributors> measureTopFiveContributorsByCommits(KStream<String, GithubCommitMessage> input) {
        KTable<String, Long> commitsPerUser = input
                .groupBy((key, value) -> value.getCommitterName())
                .count();

        return convertKTableToTopFive(commitsPerUser);
    }

    private KTable<String, Long> measureNumberOfLinesEdited(KStream<String, GithubCommitMessage> input) {
        return input.map((key, value) -> KeyValue.pair(key, (long) value.getLinesChanged()))
                .groupByKey(Grouped.with(Serdes.String(), Serdes.Long()))
                .reduce(Long::sum);
    }

    private KTable<String, Long> measureIncrementOfLines(KStream<String, GithubCommitMessage> input) {
        return input.map((key, value) -> KeyValue.pair(key, (long) value.getLinesAdded() - value.getLinesDeleted()))
                .groupByKey(Grouped.with(Serdes.String(), Serdes.Long()))
                .reduce(Long::sum);
    }

    private KTable<String, TopFiveContributors> measureTopFiveContributorsByLines(KStream<String, GithubCommitMessage> input) {
        KTable<String, Long> linesPerUser = input
                .map((key, value) -> KeyValue.pair(value.getCommitterName(), (long) value.getLinesChanged()))
                .groupBy((key, value) -> key, Grouped.with(Serdes.String(), Serdes.Long()))
                .reduce(Long::sum);

        return convertKTableToTopFive(linesPerUser);
    }

    private KTable<String, Double> measurePercentOfCommitsWithTheSameAuthorAndCommitter(KStream<String, GithubCommitMessage> input) {
        return input.map((key, value) -> KeyValue.pair(value.getCommitId(), value.getAuthorName().equals(value.getCommitterName())))
                .groupBy((key, value) -> value)
                .count()
                .groupBy((key, value) -> KeyValue.pair("stats", MyPairBooleanLong.of(key, value)),
                        Grouped.with(Serdes.String(), CustomSerdes.myPairBooleanLong()))
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
                .toTable();
    }

    private KTable<String, TopFiveContributors> convertKTableToTopFive(KTable<String, Long> linesPerUser) {
        return linesPerUser
                .groupBy((key, value) -> KeyValue.pair("all", MyPairStringLong.of(key, value)),
                        Grouped.with(Serdes.String(), CustomSerdes.myPairStringLong()))
                .aggregate(
                        TopFiveContributors::new,
                        (key, value, aggregate) -> {
                            aggregate.add((String) value.getLeft(), (Long) value.getRight());
                            return aggregate;
                        },
                        (key, value, aggregate) -> {
                            aggregate.remove((String) value.getLeft(), (Long) value.getRight());
                            return aggregate;
                        });
    }

}
