package org.example.kafkacapstoneproject.model;

import lombok.ToString;

@ToString
public class MyPairBooleanLong extends MyPair<Boolean, Long> {

    public MyPairBooleanLong(Boolean left, Long right) {
        super(left, right);
    }

    public static MyPairBooleanLong of(Boolean left, Long right) {
        return new MyPairBooleanLong(left, right);
    }
}
