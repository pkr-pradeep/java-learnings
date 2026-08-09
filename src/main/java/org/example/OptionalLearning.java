package org.example;

import java.util.Optional;

/**
 * Demonstrates best practices and key patterns when working with Java Optional.
 */
public class OptionalLearning {

    public static void main(String[] args) {
        OptionalLearning demo = new OptionalLearning();

        // 1. orElse vs orElseGet (orElse evaluates eagerly, orElseGet evaluates lazily)
        Optional<String> emptyOptional = Optional.ofNullable(demo.getName());
        String nameWithOrElse = emptyOptional.orElse("Default Java");
        String nameWithOrElseGet = emptyOptional.orElseGet(demo::getDefaultName);

        System.out.println("orElse Result: " + nameWithOrElse);
        System.out.println("orElseGet Result: " + nameWithOrElseGet);

        // 2. map & filter chaining with Optional
        Optional<String> validOptional = Optional.of("spring framework");
        validOptional
                .filter(val -> val.startsWith("spring"))
                .map(String::toUpperCase)
                .ifPresent(upperVal -> System.out.println("Processed Optional Value: " + doConcat(upperVal, "NICE")));
    }

    public String getName() {
        return null; // Represents missing value
    }

    public String getDefaultName() {
        return "Java Learning";
    }

    public static String doConcat(String primary, String stringToConcat) {
        return primary + " - " + stringToConcat;
    }
}

