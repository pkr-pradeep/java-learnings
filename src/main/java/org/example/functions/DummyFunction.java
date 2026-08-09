package org.example.functions;

import java.util.function.Function;

/**
 * Custom Functional Interface extending Java's standard Function<Integer, Double>.
 * Demonstrates Single Abstract Method (SAM) contracts in Java 8+.
 */
@FunctionalInterface
public interface DummyFunction extends Function<Integer, Double> {
    
    @Override
    Double apply(Integer integer);
}