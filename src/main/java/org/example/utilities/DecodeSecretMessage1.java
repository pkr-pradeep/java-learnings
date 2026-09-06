package org.example.utilities;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class DecodeSecretMessage1 {

    public static void main(String[] args) throws IOException {
        if (args.length < 1) return;
        String arg = args[0];
        String html = arg.startsWith("http")
                //for readAllBytes() - Language level modified to java 9
                ? new String(new URL(arg).openStream().readAllBytes(), StandardCharsets.UTF_8)
                : new String(Files.readAllBytes(Paths.get(arg)), StandardCharsets.UTF_8);
        print(parse(html));
    }

    private static List<int[]> parse(String html) {
        String text = html.replace("</td>", "\t").replace("</tr>", "\n");

        StringBuilder clean = new StringBuilder();
        boolean inTag = false;
        for (char c : text.toCharArray()) {
            if (c == '<') inTag = true;
            else if (c == '>') inTag = false;
            else if (!inTag) clean.append(c);
        }

        List<int[]> points = new ArrayList<>();
        for (String line : clean.toString().split("\n")) {
            String[] cols = line.split("\t");
            if (cols.length < 3) continue;
            try {
                int x = Integer.parseInt(cols[0].trim());
                int y = Integer.parseInt(cols[2].trim());
                char ch = cols[1].trim().charAt(0);
                points.add(new int[]{ch, x, y});
            } catch (Exception exceptionIgnored) {
                System.out.println("any other thing would be ignored");
            }
        }
        return points;
    }

    private static void print(List<int[]> points) {
        if (points.isEmpty()) return;

        int maxX = 0, maxY = 0;
        for (int[] p : points) {
            maxX = Math.max(maxX, p[1]);
            maxY = Math.max(maxY, p[2]);
        }

        char[][] grid = new char[maxY + 1][maxX + 1];
        for (char[] row : grid) Arrays.fill(row, ' ');
        for (int[] p : points) grid[maxY - p[2]][p[1]] = (char) p[0];

        for (char[] row : grid) System.out.println(new String(row));
    }
}