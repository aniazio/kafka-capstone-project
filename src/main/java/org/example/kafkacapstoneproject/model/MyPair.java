package org.example.kafkacapstoneproject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.CompareToBuilder;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyPair<T, U> implements Serializable, Comparable<MyPair<T, U>> {
    private T left;
    private U right;

    public static <T, U> MyPair<T, U> of(T left, U right) {
        return new MyPair<>(left, right);
    }


    @Override
    public int compareTo(MyPair<T, U> other) {
        return (new CompareToBuilder()).append(this.getLeft(), other.getLeft()).append(this.getRight(), other.getRight()).toComparison();
    }
}
