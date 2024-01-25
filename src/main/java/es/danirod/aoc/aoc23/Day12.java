package es.danirod.aoc.aoc23;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Day12 {

    public static void main(String[] args) throws IOException {
        var problem = new Day12("samples/12b");
        // var problem = new Day12("inputs/12");
        System.out.println("12.1: " + problem.solution1());
        System.out.println("12.2: " + problem.solution2());
    }

    private List<String> lines;

    private List<Line> maps;

    private List<Line> expands;

    public Day12(String path) throws IOException {
        this.lines = AocUtils.fileAsLines(path);
        this.maps = lines.stream().map(l -> new Line(l)).toList();
        this.expands = lines.stream().map(this::expand).map(l -> new Line(l)).toList();

        this.maps.forEach(m -> m.infer.infer());
    }

    private String expand(String line) {
        String[] parts = line.split(" ");
        StringBuilder enhanced = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            enhanced.append(parts[0]);
            if (i < 4)
                enhanced.append("?");
        }
        enhanced.append(" ");
        for (int i = 0; i < 5; i++) {
            enhanced.append(parts[1]);
            if (i < 4)
                enhanced.append(",");
        }
        return enhanced.toString();
    }

    public long solution1() {
        long count = 0;
        int i = 0;
        for (Line map : this.maps) {
            System.out.print(i++ + " / " + maps.size() + "\r");
            count += map.explorer.explore();
        }
        return count;
    }

    public long solution2() {
        long count = 0;
        int i = 0;
        for (Line map : this.expands) {
            System.out.print(i++ + " / " + maps.size() + "\r");
            count += map.explorer.explore();
        }
        return count;
    }

    private static class Line {

        String original;

        int[] parts;

        int[] checksum;

        Solver solver;

        InferRule infer;

        Explorer explorer;

        public Line(String original) {
            this.original = original;
            var tokens = original.split(" ");
            this.parts = parseParts(tokens[0]);
            this.checksum = parseChecksum(tokens[1]);
            this.solver = new Solver();
            this.infer = new InferRule();
            this.explorer = new Explorer();
        }

        private int[] parseParts(String map) {
            // Operate on a larger array, then it will be compacted.
            int[] draft = new int[map.length()];
            int length = 0;

            for (int i = 0; i < map.length(); i++) {
                char thisChar = map.charAt(i);
                char prevChar = switch (i) {
                    case 0 -> 0;
                    default -> map.charAt(i - 1);
                };

                // Carriage forward if needed
                if (i > 0 && thisChar != prevChar)
                    length++;
                if (thisChar == '#')
                    draft[length]++;
                else if (thisChar == '?')
                    draft[length]--;
            }
            length++; // last one

            // Compact the final array and return it.
            int[] result = new int[length];
            System.arraycopy(draft, 0, result, 0, length);
            return result;
        }

        private int[] parseChecksum(String cs) {
            String[] numbers = cs.split(",");
            int[] ints = new int[numbers.length];
            for (int i = 0; i < ints.length; i++)
                ints[i] = Integer.parseInt(numbers[i]);
            return ints;
        }

        public Line mutate(int pos, char value) {
            if (original.charAt(pos) != '?')
                throw new IllegalArgumentException("No question at that pos");
            var builder = new StringBuilder(original);
            builder.setCharAt(pos, value);
            String nextOriginal = builder.toString();
            return new Line(nextOriginal);
        }

        @Override
        public String toString() {
            return original
                + "\n\tparts = " + Arrays.toString(parts)
                + "\n\tchecksum = " + Arrays.toString(checksum)
                + solver.toString();
        }

        private class Solver {
            /** Position of the next question mark. */
            private final int incognita = original.indexOf('?');

            /** There are question marks at all or this is final. */
            private final boolean complete = incognita == -1;

            private final Valid validity;
            
            public Solver() {
                if (complete) {
                    validity = isValidComplete();
                } else {
                    validity = isValidIncomplete();
                }
            }

            private Valid isValidComplete() {
                var left = Arrays.toString(nozero(parts));
                var right = Arrays.toString(checksum);
                if (left.equals(right)) {
                    return Valid.Valid;
                } else {
                    return Valid.NotValid;
                }
            }


            private Valid isValidIncomplete() {
                int[] aleft = nozero(parts);
                int[] aright = checksum;
                int mleft = maxOf(aleft);
                int mright = maxOf(aright);

                // Discard cases where there is already too much consecutives.
                if (mleft > mright)
                    return Valid.NotValid;
                // None of the heuristics fail.
                return Valid.DontKnow;
            }

            @Override
            public String toString() {
                return "\n\tcomplete = " + complete
                    + "\n\tvalid = " + validity;

            }
        }

        private class InferRule {
            public boolean needed() {
                return !solver.complete;
            }

            public boolean infer() {
                if (!needed()) return false;

                System.out.println(Line.this.toString());
                return true;
            }
        }

        private class Explorer {

            public int explore() {
                int counts = 0;

                switch (solver.validity) {
                    case NotValid:
                        break;
                    case Valid:
                        counts++;
                        break;
                    case DontKnow: {
                        counts += mutate(solver.incognita, '.').explorer.explore();
                        counts += mutate(solver.incognita, '#').explorer.explore();
                        break;
                    }
                }

                return counts;
            }
        }
    }

    private enum Valid {
        Valid,
        NotValid,
        DontKnow;
    }

    static int[] nozero(int[] arr) {
        int[] draft = new int[arr.length];
        int s, t = 0;
        for (s = 0; s < arr.length; s++)
            if (arr[s] != 0)
                draft[t++] = arr[s];
        int[] clean = new int[t];
        System.arraycopy(draft, 0, clean, 0, t);
        return clean;
    }

    static int countnozero(int[] arr) {
        int t = 0;
        for (int i : arr)
            if (i != 0)
                t++;
        return t;
    }

    static int maxOf(int[] nums) {
        int m = 0;
        for (int n : nums)
            if (n > m) m = n;
        return m;
    }
}
