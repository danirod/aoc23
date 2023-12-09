package es.danirod.aoc.aoc23;

import java.io.IOException;

public class Day06 {

    private final int[] times, distances;

    private final long singleTime, singleDistance;

    public Day06(String path) throws IOException {
        var lines = AocUtils.fileAsLines(path);
        times = intArray(lines.get(0));
        distances = intArray(lines.get(1));
        singleTime = bigNumber(lines.get(0));
        singleDistance = bigNumber(lines.get(1));
    }

    private int[] intArray(String line) {
        var numbers = line.split(": ")[1].strip();
        var values = numbers.split("\\s+");
        int[] result = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = Integer.parseInt(values[i]);
        }
        return result;
    }

    private long bigNumber(String line) {
        var numbers = line.split(":")[1].strip();
        var number = numbers.replaceAll("\\s+", "");
        return Long.parseLong(number);
    }

    private long score(long duration, long hold) {
        return (duration - hold) * hold;
    }

    public int solution1() {
        int globalScore = 1;
        for (int race = 0; race < times.length; race++) {
            int localScore = 0;
            for (int h = 1; h < times[race] - 1; h++) {
                var thisScore = score(times[race], h);
                if (thisScore > distances[race]) {
                    localScore++;
                }
            }
            globalScore *= localScore;
        }
        return globalScore;
    }

    public long solution2() {
        // This is a thrown guess without any research, but I am starting to
        // believe that the score follows some kind of parabolic curve, which
        // means that there is a minimum and a maximum. Let me find the lowest
        // and highest time that breaks the record, and I will compute the
        // distance between the minimum and the maximum.
        long min = 0, max = singleTime;
        while (score(singleTime, min) <= singleDistance) {
            min++;
        }
        while (score(singleTime, max) <= singleDistance) {
            --max;
        }
        return max - min + 1;
    }

    public static void main(String[] args) throws IOException {
        var problem = new Day06("inputs/06");
        System.out.println("6.1: " + problem.solution1());
        System.out.println("6.2: " + problem.solution2());
    }

}
