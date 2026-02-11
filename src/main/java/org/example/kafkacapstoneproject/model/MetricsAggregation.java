package org.example.kafkacapstoneproject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class MetricsAggregation {

    private Long totalCommits;
    private Integer totalCommiters;
    private CommitsByLanguage commitsPerLanguage;
    private TopFiveContributors topFiveContributorsByCommits;
    private Long numberOfLines;
    private Long incrementOfLines;
    private TopFiveContributors topFiveContributorsByLines;
    private Double percentOfCommitsWithTheSameAuthorAndCommitter;

    public MetricsAggregation(Long totalCommits, Integer totalCommiters) {
        this.totalCommits = totalCommits;
        this.totalCommiters = totalCommiters;
    }
}
