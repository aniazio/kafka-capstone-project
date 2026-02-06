package org.example.kafkacapstoneproject.model;

import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

import java.util.TreeSet;

@Data
public class TopFiveContributors {

    TreeSet<Pair<Long, String>> namesByCommits = new TreeSet<>((o1, o2) -> {
        final int result = o2.getLeft().compareTo(o1.getLeft());
        if (result != 0) return result;
        else {
            return o2.getRight().compareTo(o1.getRight());
        }
    });

    public void add(String name, Long commits) {
        if (namesByCommits.size() >= 5) {
            this.namesByCommits.remove(this.namesByCommits.last());
        }
        this.namesByCommits.add(Pair.of(commits, name));
    }

    public void remove(String name, Long commits) {
        this.namesByCommits.remove(Pair.of(name, commits));
    }

}
