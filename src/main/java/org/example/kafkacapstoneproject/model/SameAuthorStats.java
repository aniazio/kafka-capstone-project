package org.example.kafkacapstoneproject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SameAuthorStats implements Serializable {
    private long total;
    private long sameAuthor;
    private double percent;

    public void addStat(long number, boolean isSameAuthor) {
        this.total += number;
        if (isSameAuthor) {
            this.sameAuthor += number;
        }
        percent = ((double) sameAuthor / total) * 100;
    }

    public void removeStat(long number, boolean isSameAuthor) {
        this.total -= number;
        if (isSameAuthor) {
            this.sameAuthor -= number;
        }
        percent = ((double) sameAuthor / total) * 100;
    }
}
