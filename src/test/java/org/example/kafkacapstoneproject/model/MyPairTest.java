package org.example.kafkacapstoneproject.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MyPairTest {

    @Test
    void testCompareTo() {
        MyPair<Long, String> pair1 = MyPair.of(1L, "John");
        MyPair<Long, String> pair2 = MyPair.of(2L, "Mark");
        MyPair<Long, String> pair3 = MyPair.of(2L, "Zigi");

        assertTrue(pair1.compareTo(pair2) < 0);
        assertTrue(pair2.compareTo(pair3) < 0);
    }
}
