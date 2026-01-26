package org.example.kafkacapstoneproject.message;

import lombok.Data;

import java.time.Duration;

@Data
public class GitHubAccountMessage {

    private String accountName;
    private Duration interval;
}
