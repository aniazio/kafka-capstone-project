package org.example.kafkacapstoneproject.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TopFiveContributorsTest {

    private String name = "Anna";
    private TopFiveContributors topFiveContributors;

    @BeforeEach
    void setUp() {
        topFiveContributors = new TopFiveContributors();
        topFiveContributors.add(name, 2L);
        topFiveContributors.add(name, 3L);
        topFiveContributors.add(name, 4L);
        topFiveContributors.add(name, 5L);
        topFiveContributors.add(name, 7L);
    }

    @Test
    void testAddWhenAddedSmallest() {
        topFiveContributors.add(name, 1L);
        assertEquals(5, topFiveContributors.namesByCommits.size());
        assertEquals(2L, topFiveContributors.namesByCommits.last().getLeft());
        assertEquals(7L, topFiveContributors.namesByCommits.first().getLeft());
    }

    @Test
    void testAddWhenAddedInTheMiddle() {
        topFiveContributors.add(name, 6L);
        assertEquals(5, topFiveContributors.namesByCommits.size());
        assertEquals(3L, topFiveContributors.namesByCommits.last().getLeft());
        assertEquals(7L, topFiveContributors.namesByCommits.first().getLeft());
    }

    @Test
    void testAddWhenAddedTop() {
        topFiveContributors.add(name, 10L);
        assertEquals(5, topFiveContributors.namesByCommits.size());
        assertEquals(3L, topFiveContributors.namesByCommits.last().getLeft());
        assertEquals(10L, topFiveContributors.namesByCommits.first().getLeft());
    }

    @Test
    void testRemoveWhenFirst() {
        topFiveContributors.remove(name, 2L);
        assertEquals(4, topFiveContributors.namesByCommits.size());
        assertEquals(3L, topFiveContributors.namesByCommits.last().getLeft());
        assertEquals(7L, topFiveContributors.namesByCommits.first().getLeft());
    }

    @Test
    void testRemoveWhenInTheMiddle() {
        topFiveContributors.remove(name, 5L);
        assertEquals(4, topFiveContributors.namesByCommits.size());
        assertEquals(2L, topFiveContributors.namesByCommits.last().getLeft());
        assertEquals(7L, topFiveContributors.namesByCommits.first().getLeft());
    }

    @Test
    void testRemoveWhenLast() {
        topFiveContributors.remove(name, 7L);
        assertEquals(4, topFiveContributors.namesByCommits.size());
        assertEquals(2L, topFiveContributors.namesByCommits.last().getLeft());
        assertEquals(5L, topFiveContributors.namesByCommits.first().getLeft());
    }
}
