package org.example.functions;

import java.util.function.Function;

public class FunctionImplementation {

    public static void main(String args[])
    {
        // Function which takes in a number
        // and returns half of it
        DummyFunction doHalfOnSecond = x ->  {
            x = x/2;
            return x.doubleValue();
        };

        Function<Integer, Double> lastlyAddThree = doHalfOnSecond.andThen(x -> x + 3);

        Function<Double, Double> plusOneFirst = lastlyAddThree.compose(x -> {
            x = x + 1.0;
            return x.intValue();
        });
        // Applying the function to get the result
        //Example 7.0 -> 7.0 + 1.0 = 8.0 -> 8 -> 8/2 -> 4 -> 4 + 3 = 7 [apply will convert integer to Double]
        //10.0 -> it would 8.0 [Result]
        System.out.println(plusOneFirst.apply(10.0));
    }
}
