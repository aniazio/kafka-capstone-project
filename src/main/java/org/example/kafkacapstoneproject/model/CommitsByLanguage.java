package org.example.kafkacapstoneproject.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class CommitsByLanguage {
    Map<String, Long> commitsPerLanguage = new HashMap<>();

    public void put(String language, Long commits) {
        commitsPerLanguage.put(language, commits);
    }

    public void remove(String language, Long commits) {
        commitsPerLanguage.remove(language, commits);
    }
}
