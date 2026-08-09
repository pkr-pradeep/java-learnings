package org.example.functions;

import java.util.function.Function;

/**
 * Demonstrates functional composition using Function.andThen() and Function.compose().
 * 
 * - andThen(after): Applies current function FIRST, then applies 'after' function to the result.
 * - compose(before): Applies 'before' function FIRST, then applies current function to the result.
 */
public class FunctionImplementation {

    public static void main(String[] args) {
        // Function 1: Halves an Integer and returns Double
        DummyFunction halfFunction = x -> (x / 2.0);

        // Chain with andThen: halfFunction FIRST, then add 3.0
        Function<Integer, Double> halfThenAddThree = halfFunction.andThen(x -> x + 3.0);

        // Chain with compose: plusOneFirst FIRST (convert Double + 1 to int), then halfThenAddThree
        Function<Double, Double> fullPipeline = halfThenAddThree.compose(val -> (int) (val + 1.0));

        // Execution order for input 10.0:
        // 1. compose step: 10.0 + 1.0 = 11.0 -> (int) 11
        // 2. halfFunction: 11 / 2.0 = 5.5
        // 3. andThen step: 5.5 + 3.0 = 8.5
        Double result = fullPipeline.apply(10.0);
        System.out.println("Pipeline Result for input 10.0: " + result);
    }
}

