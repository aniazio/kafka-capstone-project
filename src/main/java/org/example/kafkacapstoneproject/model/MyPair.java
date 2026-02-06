package org.example.kafkacapstoneproject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyPair<T, U> implements Serializable {
    private T left;
    private U right;

    public static <T, U> MyPair<T, U> of(T left, U right) {
        return new MyPair<>(left, right);
    }
}
