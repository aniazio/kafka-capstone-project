package org.example.kafkacapstoneproject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Comparator;
import java.util.TreeSet;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TopFiveContributors implements Serializable {

    TreeSet<MyPair<Long, String>> namesByCommits = new TreeSet<>(Comparator.reverseOrder());

    public void add(String name, Long commits) {
        this.namesByCommits.add(MyPair.of(commits, name));
        if (namesByCommits.size() > 5) {
            this.namesByCommits.remove(this.namesByCommits.last());
        }
    }

    public void remove(String name, Long commits) {
        this.namesByCommits.remove(MyPair.of(commits, name));
    }

}
