package org.example.kafkacapstoneproject.model;

import lombok.ToString;

@ToString
public class MyPairStringLong extends MyPair<String, Long> {

    public MyPairStringLong(String left, Long right) {
        super(left, right);
    }

    public static MyPairStringLong of(String left, Long right) {
        return new MyPairStringLong(left, right);
    }
}
