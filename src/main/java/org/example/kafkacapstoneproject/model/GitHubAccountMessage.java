package org.example.kafkacapstoneproject.model;

import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Slf4j
public class GitHubAccountMessage implements Serializable {

    private String accountName;
    private String date;

    public static GitHubAccountMessage buildFromCsv(String message) {
        if (message.startsWith("\"") && message.endsWith("\"")) {
            message = message.substring(1, message.length() - 1);
        }
        String[] split = message.split(",");
        if (split.length != 2 || StringUtils.isBlank(split[0]) || StringUtils.isBlank(split[1])) {
            log.error("Invalid message: {}", message);
            return null;
        }


        return GitHubAccountMessage.builder()
                .accountName(split[0].trim())
                .date(getDate(split[1].trim()).atZone(ZoneId.of(ZoneOffset.UTC.getId())).format(DateTimeFormatter.ISO_LOCAL_DATE))
                .build();
    }

    private static Instant getDate(String s) {
        LocalDateTime now = LocalDateTime.now();
        String timeUnit = s.substring(s.length() - 1).toLowerCase();
        long value = Long.valueOf(s.substring(0, s.length() - 1));
        return switch (timeUnit) {
            case "y" -> now.minusYears(value).toInstant(ZoneOffset.UTC);
            case "w" -> now.minusWeeks(value).toInstant(ZoneOffset.UTC);
            case "d" -> now.minusDays(value).toInstant(ZoneOffset.UTC);
            case "h" -> now.minusHours(value).toInstant(ZoneOffset.UTC);
            case "m" -> now.minusMinutes(value).toInstant(ZoneOffset.UTC);
            case "s" -> now.minusSeconds(value).toInstant(ZoneOffset.UTC);
            default -> throw new IllegalArgumentException("Invalid time unit: " + timeUnit);
        };
    }
}
