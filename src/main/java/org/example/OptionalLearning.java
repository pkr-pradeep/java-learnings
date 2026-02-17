package org.example;

import java.util.Optional;

public class OptionalLearning {

    public static void main(String[] args) {
        Optional<String> optional = Optional.ofNullable(new OptionalLearning().getName());
        //String name = optional.orElse("Java");
        optional.orElseGet(() -> new OptionalLearning().getDefaultName());
        optional.ifPresent(x -> System.out.println(OptionalLearning.doConcat(x, " - nice")));
    }

    public String getName() {
        return null;
    }

    public String getDefaultName() {
        return "Java";
    }

    public static String doConcat(String primary, String stringToConcat) {
        return primary + " " +  stringToConcat;
    }
}
