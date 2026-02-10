package org.example.kafkacapstoneproject.model;

import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GitHubAccountMessage implements Serializable {

    private String accountName;
    private Date date;

    public static GitHubAccountMessage buildFromCsv(String message) {
        String[] split = message.split(",");
        if (split.length != 2 || StringUtils.isBlank(split[0]) || StringUtils.isBlank(split[1])) {
            return null;
        }


        return GitHubAccountMessage.builder()
                .accountName(split[0].trim())
                .date(Date.from(getDate(split[1]).toInstant(ZoneOffset.UTC)))
                .build();
    }

    private static LocalDateTime getDate(String s) {
        LocalDateTime now = LocalDateTime.now();
        String timeUnit = s.substring(s.length() - 1).toLowerCase();
        long value = Long.valueOf(s.substring(0, s.length() - 1));

        return switch (timeUnit) {
            case "y" -> now.minusYears(value);
            case "w" -> now.minusWeeks(value);
            case "d" -> now.minusDays(value);
            case "h" -> now.minusHours(value);
            case "m" -> now.minusMinutes(value);
            case "s" -> now.minusSeconds(value);
            default -> throw new IllegalArgumentException("Invalid time unit: " + timeUnit);
        };
    }
}
