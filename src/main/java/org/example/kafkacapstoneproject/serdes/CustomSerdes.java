package org.example.kafkacapstoneproject.serdes;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.example.kafkacapstoneproject.model.CommitsByLanguage;
import org.example.kafkacapstoneproject.model.GitHubAccountMessage;
import org.example.kafkacapstoneproject.model.GithubCommitMessage;
import org.example.kafkacapstoneproject.model.MetricsAggregation;
import org.example.kafkacapstoneproject.model.MyPairBooleanLong;
import org.example.kafkacapstoneproject.model.MyPairStringLong;
import org.example.kafkacapstoneproject.model.SameAuthorStats;
import org.example.kafkacapstoneproject.model.TopFiveContributors;

import java.util.HashSet;

public final class CustomSerdes {
    private CustomSerdes() {
    }

    public static Serde<GitHubAccountMessage> gitHubAccountMessage() {
        JsonSerializer<GitHubAccountMessage> serializer = new JsonSerializer<>();
        JsonDeserializer<GitHubAccountMessage> deserializer = new JsonDeserializer<>(GitHubAccountMessage.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }

    public static Serde<GithubCommitMessage> githubCommitMessage() {
        JsonSerializer<GithubCommitMessage> serializer = new JsonSerializer<>();
        JsonDeserializer<GithubCommitMessage> deserializer = new JsonDeserializer<>(GithubCommitMessage.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }

    public static Serde<MetricsAggregation> metricsAggregation() {
        JsonSerializer<MetricsAggregation> serializer = new JsonSerializer<>();
        JsonDeserializer<MetricsAggregation> deserializer = new JsonDeserializer<>(MetricsAggregation.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }

    public static Serde<SameAuthorStats> sameAuthorStats() {
        JsonSerializer<SameAuthorStats> serializer = new JsonSerializer<>();
        JsonDeserializer<SameAuthorStats> deserializer = new JsonDeserializer<>(SameAuthorStats.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }

    public static Serde<TopFiveContributors> topFiveContributors() {
        JsonSerializer<TopFiveContributors> serializer = new JsonSerializer<>();
        JsonDeserializer<TopFiveContributors> deserializer = new JsonDeserializer<>(TopFiveContributors.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }

    public static Serde<MyPairStringLong> myPairStringLong() {
        JsonSerializer<MyPairStringLong> serializer = new JsonSerializer<>();
        JsonDeserializer<MyPairStringLong> deserializer = new JsonDeserializer<>(MyPairStringLong.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }

    public static Serde<MyPairBooleanLong> myPairBooleanLong() {
        JsonSerializer<MyPairBooleanLong> serializer = new JsonSerializer<>();
        JsonDeserializer<MyPairBooleanLong> deserializer = new JsonDeserializer<>(MyPairBooleanLong.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }

    public static Serde<CommitsByLanguage> commitsByLanguage() {
        JsonSerializer<CommitsByLanguage> serializer = new JsonSerializer<>();
        JsonDeserializer<CommitsByLanguage> deserializer = new JsonDeserializer<>(CommitsByLanguage.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }

    public static Serde<HashSet> hashSet() {
        JsonSerializer<HashSet> serializer = new JsonSerializer<>();
        JsonDeserializer<HashSet> deserializer = new JsonDeserializer<>(HashSet.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }
}
