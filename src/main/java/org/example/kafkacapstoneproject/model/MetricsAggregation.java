package org.example.kafkacapstoneproject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.Map;

@Data
@AllArgsConstructor
@ToString
public class MetricsAggregation {

    private Long totalCommits;
    private Long totalCommiters;
    private Map<String, Long> commitsPerLanguage;
    private TopFiveContributors topFiveContributorsByCommits;
    private Long numberOfLines;
    private Long incrementOfLines;
    private TopFiveContributors topFiveContributorsByLines;
    private Double percentOfCommitsWithTheSameAuthorAndCommitter;

    public MetricsAggregation(Long totalCommits, Long totalCommiters) {
        this.totalCommits = totalCommits;
        this.totalCommiters = totalCommiters;
    }
}
