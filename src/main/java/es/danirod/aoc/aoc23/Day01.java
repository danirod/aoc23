package es.danirod.aoc.aoc23;

import java.io.IOException;

public class Day01 {

    private String path;

    public Day01(String path) {
        this.path = path;
    }

    String[] words = {"one", "two", "three", "four", "five", "six", "seven", "eight", "nine"};

    private int findFirstOcurrence(String line) {
        for (int i = 0; i < line.length(); i++) {
            var substr = line.substring(i);

            // Easy case: it starts with a digit.
            if (Character.isDigit(substr.charAt(0))) {
                return substr.charAt(0) - '0';
            }

            // Complex case: it starts with one of the word in words.
            for (int w = 0; w < words.length; w++) {
                if (substr.startsWith(words[w])) {
                    return w + 1;
                }
            }
        }

        return 0;
    }

    private int findLastOcurrence(String line) {
        for (int l = line.length(); l >= 0; --l) {
            var substr = line.substring(0, l);

            // Easy case: it ends with a digit.
            if (Character.isDigit(substr.charAt(l - 1))) {
                return substr.charAt(l - 1) - '0';
            }

            // Complex case: it ends with one of the word in words.
            for (int w = 0; w < words.length; w++) {
                if (substr.endsWith(words[w])) {
                    return w + 1;
                }
            }
        }

        return 0;
    }

    private int firstLastNumberOrWord(String line) {
        var first = findFirstOcurrence(line);
        var last = findLastOcurrence(line);
        return first * 10 + last;
    }

    public int solution1() throws IOException {
        var lines = AocUtils.fileAsLineStream(this.path);
        return lines.mapToInt((line) -> {
            var onlyDigits = line.chars().filter(Character::isDigit).boxed().toList();
            var first = onlyDigits.get(0) - '0';
            var last = onlyDigits.get(onlyDigits.size() - 1) - '0';
            return first * 10 + last;
        }).sum();
    }

    public int solution2() throws IOException {
        var lines = AocUtils.fileAsLineStream(this.path);
        return lines.mapToInt(this::firstLastNumberOrWord).sum();
    }

    public static void main(String args[]) throws IOException {
        var problem = new Day01("inputs/01");
        System.out.println("1.1: " + problem.solution1());
        System.out.println("1.2: " + problem.solution2());
    }
}
