package org.example.csv;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UTF8EncoderChecker {

    public static void main(String[] args) {
        String name = "İnanç Esasları\n" +
                "Ä°nanÃ§ EsaslarÄ±\n" +
                "æ���� ����";

        String name2 = "pradeep";
        try {
            String regexPattern = "[^\\x00-\\x7F]+";
            Pattern pattern = Pattern.compile(regexPattern);
            Matcher matcher = pattern.matcher(name2);
            if(matcher.find()) {
                System.out.println("invalid string");
            } else {
                System.out.println("valid string");
            }
        } catch (Exception e) {
            System.out.println("error in matching");
        }
    }
}
