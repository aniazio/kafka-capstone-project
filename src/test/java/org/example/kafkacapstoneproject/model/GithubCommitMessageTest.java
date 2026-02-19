package org.example.kafkacapstoneproject.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class GithubCommitMessageTest {

    @Test
    void testRandomIsNotEmpty() {
        GithubCommitMessage result = GithubCommitMessage.random();
        assertNotNull(result);
        assertNotNull(result.getCommitId());
        assertNotNull(result.getAuthorName());
        assertNotNull(result.getCommitDate());
        assertNotNull(result.getCommitterName());
        assertNotNull(result.getLanguage());
    }
}
