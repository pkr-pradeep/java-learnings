package org.example.functions;

import java.util.function.Function;

public interface DummyFunction extends Function<Integer, Double> {
    //All others are whether default or static method.
    Double apply(Integer integer);
}