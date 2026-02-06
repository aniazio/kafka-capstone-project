package org.example.kafkacapstoneproject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.kohsuke.github.GHCommit;

import java.io.IOException;
import java.util.Date;

@Data
@AllArgsConstructor
public class GithubCommitMessage {

    private String commitId;
    private String language;
    private String authorName;
    private Date commitDate;
    private String committerName;
    private int linesAdded;
    private int linesDeleted;
    private int linesChanged;

    public GithubCommitMessage(GHCommit commit, String language) throws IOException {
        this.commitId = commit.getSHA1();
        this.authorName = commit.getAuthor().getName();
        this.commitDate = commit.getCommitDate();
        this.committerName = commit.getCommitter().getName();
        this.linesAdded = commit.getLinesAdded();
        this.linesDeleted = commit.getLinesDeleted();
        this.linesChanged = commit.getLinesChanged();
        this.language = language;
    }

}
