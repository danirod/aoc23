package es.danirod.aoc.aoc23;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class Day08 {

    private NodeMap map;

    public Day08(String file) throws IOException {
        this.map = parse(file);
    }

    private NodeMap parse(String file) throws IOException {
        var lines = new ArrayList<>(AocUtils.fileAsLines(file));
        var chain = lines.remove(0);

        var pattern = Pattern.compile("(\\w+) = \\((\\w+), (\\w+)\\)");

        Map<String, String[]> nodes = new HashMap<>();
        for (String line : lines) {
            if (line.isBlank())
                continue;
            var matcher = pattern.matcher(line);
            if (matcher.find()) {
                var currentNode = matcher.group(1);
                var left = matcher.group(2);
                var right = matcher.group(3);
                nodes.put(currentNode, new String[]{left, right});
            }
        }
        return new NodeMap(chain, nodes);
    }

    private record NodeMap(String chain, Map<String, String[]> nodes) {
        public String next(String current, char lr) {
            String[] node = nodes.get(current);
            if (node != null) {
                return lr == 'L' ? node[0] : node[1];
            }
            return null;
        }

        public String[] ghostStarts() {
            return nodes.keySet().stream().filter(key -> key.charAt(2) == 'A').toList().toArray(new String[0]);
        }
    }

    /** Optimized path. Stops when finds a loop. */
    class Trail {

        /** The collection of visited keys. Used to detect loops. */
        private Set<String> visited = new HashSet<>();

        /** The ordered collection of movement. */
        private List<String> sequence = new ArrayList<>();
        
        /** Once a loop is detected, where to go back. */
        private int returns = -1;

        /** Just for debug purposes. */
        private String initial;

        @Override
        public String toString() {
            return String.format("Trail { %s | %s, returns = %d }", initial, sequence.toString(), returns);
        }

        public Trail(String initial) {
            this.initial = initial;
            int step = 0;
            String current = initial;
            int chainLength = map.chain.length();

            while (returns == -1) {
                int charPosition = step % chainLength;
                char direction = map.chain.charAt(charPosition);
                String next = map.next(current, direction);
                String movement = current + "." + charPosition + "." + next;

                if (visited.contains(movement)) {
                    // We have already made this movement in the past
                    returns = sequence.indexOf(movement);
                } else {
                    // Not visited, add it to the sequence.
                    sequence.add(movement);
                    visited.add(movement);
                }

                // Prepare for next iteration
                current = next;
                step++;
            }
        }

        public String at(long index) {
            // If it is part of the sequence, that's it.
            if (index < sequence.size()) {
                return sequence.get((int) index).substring(0, 3);
            }

            // Then it is part of a loop.
            long offset = index - returns;
            long pos = returns + (offset % loopLength());
            // Don't ask me about this math, I did this on paper
            return sequence.get((int) pos).substring(0, 3);
        }

        public int winsAt() {
            for (int i = 0; i < sequence.size(); i++)
                if (sequence.get(i).charAt(2) == 'Z')
                    return i;
            return -1;
        }

        /** How many items are repeated on each chain. */
        public int loopLength() {
            return sequence.size() - returns;
        }
    }

    public int solution1() {
        int steps = 0;
        String current = "AAA";
        do {
            int idx = steps % map.chain.length();
            current = map.next(current, map.chain.charAt(idx));
            steps++;
        } while (!current.equals("ZZZ"));
        return steps;
    }

    public long solution2() {
        var trails = map.nodes.keySet().stream()
            .filter(k -> k.charAt(2) == 'A')
            .map(t -> new Trail(t))
            .map(Trail::winsAt)
            .toList();
        long res = trails.get(0);
        for (int i = 1; i < trails.size(); i++) {
            res = AocUtils.mcm(res, trails.get(i));
        }
        return res;
    }

    public static void main(String[] args) throws IOException {
        var problem = new Day08("inputs/08");
        System.out.println("8.1: " + problem.solution1());
        System.out.println("8.2: " + problem.solution2());
    }
}
