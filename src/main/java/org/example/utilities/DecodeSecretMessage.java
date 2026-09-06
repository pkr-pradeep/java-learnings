package org.example.utilities;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

public class DecodeSecretMessage {


    public static void decode(String url) {
        try {
            String content = fetchUrl(url);
            printGrid(parse(content));
        } catch (IOException e) {
            System.err.println("Error fetching or decoding document: " + e.getMessage());
        }
    }

    public static void decodeFile(String path) {
        try {
            String content = new String(
                    java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(path)),
                    "UTF-8"
            );
            printGrid(parse(content));
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    private static void printGrid(List<int[]> points) {
        if (points.isEmpty()) {
            System.out.println();
            return;
        }

        int maxX = 0;
        int maxY = 0;
        for (int[] p : points) {
            if (p[1] > maxX) maxX = p[1];
            if (p[2] > maxY) maxY = p[2];
        }

        int width = maxX + 1;
        int height = maxY + 1;

        char[][] grid = new char[height][width];
        for (char[] row : grid) {
            Arrays.fill(row, ' ');
        }

        for (int[] p : points) {
            int codePoint = p[0];
            int x = p[1];
            int y = p[2];
            grid[height - 1 - y][x] = (char) codePoint;
        }

        StringBuilder sb = new StringBuilder();
        for (char[] row : grid) {
            sb.append(row).append('\n');
        }
        System.out.print(sb.toString());
    }

    private static String fetchUrl(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");

        try (InputStream in = conn.getInputStream();
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(in, "UTF-8"))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            return sb.toString();
        } finally {
            conn.disconnect();
        }
    }

    private static List<int[]> parse(String content) {
        List<int[]> points = new ArrayList<>();
        String text = stripHtml(content);
        Pattern rowPattern = Pattern.compile(
                "(?<x>\\d+)\\s+(?<ch>\\S+)\\s+(?<y>\\d+)"
        );
        Matcher m = rowPattern.matcher(text);
        while (m.find()) {
            String chField = m.group("ch");
            int x = Integer.parseInt(m.group("x"));
            int y = Integer.parseInt(m.group("y"));

            int codePoint = parseCodePoint(chField);
            if (codePoint < 0) continue;
            points.add(new int[]{ codePoint, x, y });
        }
        return points;
    }

    private static String stripHtml(String content) {
        String withBreaks = content
                .replaceAll("(?i)</td>", " ")
                .replaceAll("(?i)</tr>", "\n");
        String noTags = withBreaks.replaceAll("(?s)<[^>]+>", " ");
        String decoded = noTags
                .replace("&nbsp;", " ")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&#39;", "'")
                .replace("&apos;", "'");

        // Decode any remaining numeric entities, e.g. &#9617; or &#x2591;
        Matcher ent = Pattern.compile("&#x?([0-9a-fA-F]+);").matcher(decoded);
        StringBuilder sb = new StringBuilder();
        while (ent.find()) {
            int radix = ent.group(0).toLowerCase().startsWith("&#x") ? 16 : 10;
            int cp;
            try {
                cp = Integer.parseInt(ent.group(1), radix);
            } catch (NumberFormatException e) {
                ent.appendReplacement(sb, Matcher.quoteReplacement(ent.group(0)));
                continue;
            }
            ent.appendReplacement(sb, Matcher.quoteReplacement(new String(Character.toChars(cp))));
        }
        ent.appendTail(sb);
        return sb.toString();
    }

    private static int parseCodePoint(String field) {
        if (field == null || field.isEmpty()) return -1;

        Matcher hex = Pattern.compile("(?:\\\\u|U\\+)([0-9a-fA-F]{1,6})").matcher(field);
        if (hex.matches()) {
            try {
                return Integer.parseInt(hex.group(1), 16);
            } catch (NumberFormatException e) {
                return -1;
            }
        }

        Matcher ent = Pattern.compile("&#x?([0-9a-fA-F]+);").matcher(field);
        if (ent.matches()) {
            try {
                return Integer.parseInt(ent.group(1),
                        field.toLowerCase().startsWith("&#x") ? 16 : 10);
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        int cp = field.codePointAt(0);
        return cp;
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            return;
        }
        String arg = args[0];
        decode(arg);
    }
}