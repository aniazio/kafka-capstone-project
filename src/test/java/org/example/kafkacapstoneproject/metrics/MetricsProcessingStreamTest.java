package org.example.kafkacapstoneproject.metrics;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.example.kafkacapstoneproject.config.AppConfig;
import org.example.kafkacapstoneproject.model.GithubCommitMessage;
import org.example.kafkacapstoneproject.model.MetricsAggregation;
import org.example.kafkacapstoneproject.serdes.CustomSerdes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MetricsProcessingStreamTest {

    private TopologyTestDriver topologyTestDriver;
    private TestInputTopic<String, GithubCommitMessage> inputTopic;
    private TestOutputTopic<String, MetricsAggregation> outputTopic;
    private MetricsProcessingStream metricsProcessingStream = new MetricsProcessingStream();
    private Serde<GithubCommitMessage> githubCommitMessageSerde;
    private Serde<MetricsAggregation> metricsAggregationSerde;

    @BeforeEach
    void setUp() {
        StreamsBuilder builder = new StreamsBuilder();
        metricsProcessingStream.process(builder);

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "test");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);

        topologyTestDriver = new TopologyTestDriver(builder.build(), props);

        githubCommitMessageSerde = CustomSerdes.githubCommitMessage();
        metricsAggregationSerde = CustomSerdes.metricsAggregation();

        inputTopic = topologyTestDriver.createInputTopic(AppConfig.GITHUB_COMMITS_TOPIC, new StringSerializer(), githubCommitMessageSerde.serializer());
        outputTopic = topologyTestDriver.createOutputTopic(AppConfig.GITHUB_METRICS_TOPIC, new StringDeserializer(), metricsAggregationSerde.deserializer());
    }

    @AfterEach
    void tearDown() {
        if (topologyTestDriver != null) {
            topologyTestDriver.close();
        }
        if (githubCommitMessageSerde != null) {
            githubCommitMessageSerde.close();
        }
        if (metricsAggregationSerde != null) {
            metricsAggregationSerde.close();
        }
    }

    @Test
    void testProcessWhenOneMessage() {
        GithubCommitMessage commit = GithubCommitMessage.builder()
                .commitId("commitId")
                .authorName("authorName")
                .commitDate(Date.from(Instant.now()))
                .committerName("committerName")
                .linesAdded(10)
                .linesDeleted(1)
                .linesChanged(20)
                .language("language")
                .build();
        inputTopic.pipeInput(commit);

        List<MetricsAggregation> resultList = outputTopic.readValuesToList();
        MetricsAggregation result = resultList.get(resultList.size() - 1);

        assertEquals(1, result.getTotalCommits());
        assertEquals(1, result.getTotalCommiters());
        assertEquals(1, result.getCommitsPerLanguage().getCommitsPerLanguage().get("language"));
        assertEquals(1, result.getCommitsPerLanguage().getCommitsPerLanguage().size());
        assertEquals(1, result.getTopFiveContributorsByCommits().getNamesByCommits().size());
        assertEquals("committerName", result.getTopFiveContributorsByCommits().getNamesByCommits().pollLast().getRight());
        assertEquals(20, result.getNumberOfLines());
        assertEquals(9, result.getIncrementOfLines());
        assertEquals(1, result.getTopFiveContributorsByLines().getNamesByCommits().size());
        assertEquals("committerName", result.getTopFiveContributorsByLines().getNamesByCommits().pollLast().getRight());
        assertEquals(0, result.getPercentOfCommitsWithTheSameAuthorAndCommitter());
    }

    @Test
    void testProcessWhenTwoCommiters() {
        GithubCommitMessage commit1 = GithubCommitMessage.builder()
                .commitId("commitId")
                .authorName("committerName1")
                .commitDate(Date.from(Instant.now()))
                .committerName("committerName1")
                .linesAdded(10)
                .linesDeleted(2)
                .linesChanged(20)
                .language("Java")
                .build();
        GithubCommitMessage commit2 = GithubCommitMessage.builder()
                .commitId("commitId")
                .authorName("authorName1")
                .commitDate(Date.from(Instant.now()))
                .committerName("committerName1")
                .linesAdded(10)
                .linesDeleted(1)
                .linesChanged(20)
                .language("C")
                .build();
        GithubCommitMessage commit3 = GithubCommitMessage.builder()
                .commitId("commitId")
                .authorName("authorName3")
                .commitDate(Date.from(Instant.now()))
                .committerName("committerName2")
                .linesAdded(200)
                .linesDeleted(10)
                .linesChanged(190)
                .language("Java")
                .build();
        inputTopic.pipeInput(commit1);
        inputTopic.pipeInput(commit2);
        inputTopic.pipeInput(commit3);

        List<MetricsAggregation> resultList = outputTopic.readValuesToList();
        MetricsAggregation result = resultList.get(resultList.size() - 1);

        assertEquals(3, result.getTotalCommits());
        assertEquals(2, result.getTotalCommiters());
        assertEquals(2, result.getCommitsPerLanguage().getCommitsPerLanguage().get("Java"));
        assertEquals(1, result.getCommitsPerLanguage().getCommitsPerLanguage().get("C"));
        assertEquals(2, result.getCommitsPerLanguage().getCommitsPerLanguage().size());
        assertEquals(2, result.getTopFiveContributorsByCommits().getNamesByCommits().size());
        assertEquals("committerName1", result.getTopFiveContributorsByCommits().getNamesByCommits().pollLast().getRight());
        assertEquals("committerName2", result.getTopFiveContributorsByCommits().getNamesByCommits().pollLast().getRight());
        assertEquals(230, result.getNumberOfLines());
        assertEquals(207, result.getIncrementOfLines());
        assertEquals(2, result.getTopFiveContributorsByLines().getNamesByCommits().size());
        assertEquals("committerName2", result.getTopFiveContributorsByLines().getNamesByCommits().pollLast().getRight());
        assertEquals("committerName1", result.getTopFiveContributorsByLines().getNamesByCommits().pollLast().getRight());
        assertEquals(33, Math.floor(result.getPercentOfCommitsWithTheSameAuthorAndCommitter()));
    }
}
