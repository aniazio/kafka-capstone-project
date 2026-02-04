package org.example.kafkacapstoneproject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.kohsuke.github.GHCommit;

@Data
@AllArgsConstructor
public class GithubCommitMessage {

    private GHCommit commit;
    private String language;

}
