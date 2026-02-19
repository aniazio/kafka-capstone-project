package org.example.kafkacapstoneproject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.kohsuke.github.GHCommit;

import java.io.IOException;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GithubCommitMessage implements Serializable {

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

    public GithubCommitMessage(Map<String, Object> githubResponse) {
        this.commitId = (String) githubResponse.get("sha");
        this.authorName = (String) ((Map<String, Object>) ((Map<String, Object>) githubResponse.get("commit")).get("author")).get("name");
        this.commitDate = new Date();
        this.committerName = (String) ((Map<String, Object>) ((Map<String, Object>) githubResponse.get("commit")).get("committer")).get("name");
        this.linesAdded = 0;
        this.linesDeleted = 0;
        this.linesChanged = 0;
        this.language = language;
    }

    private static final Random random = new Random();
    private static List<String> authors = List.of("John", "Mark", "Anna", "Simon", "Sara", "Alfred", "Barbara");
    private static List<String> languages = List.of("Java", "Python", "C++", "C#", "JavaScript");

    public static GithubCommitMessage random() {
        GithubCommitMessage result = new GithubCommitMessage();
        result.setCommitId(UUID.randomUUID().toString());
        result.setAuthorName(authors.get(random.nextInt(authors.size())));
        result.setCommitDate(Date.from(Instant.now()));
        result.setCommitterName(authors.get(random.nextInt(authors.size())));
        result.setLinesAdded(random.nextInt(100));
        result.setLinesDeleted(random.nextInt(100));
        result.setLinesChanged(random.nextInt(100));
        result.setLanguage(languages.get(random.nextInt(languages.size())));
        return result;
    }
}
