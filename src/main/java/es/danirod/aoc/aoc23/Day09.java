package es.danirod.aoc.aoc23;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Day09 {

    public class Grid {

        private long[][] values;
        
        public Grid(String line) {
            String[] tokens = line.split(" ");
            values = new long[tokens.length][tokens.length];
            for (int i = 0; i < tokens.length; i++) {
                values[0][i] = Long.parseLong(tokens[i]);
            }
            for (int y = 1; y < values.length; y++) {
                int max = values.length - y;
                for (int x = 0; x < max; x++) {
                    values[y][x] = values[y-1][x+1] - values[y-1][x];
                }
            }
        }

        public long next() {
            long ac = 0;
            for (int i = 0; i < values.length; i++) {
                ac += values[values.length - i - 1][i];
            }
            return ac;
        }

        public long prev() {
            long prev = 0;
            for (int i = values.length - 1; i >= 0; i--) {
                prev = values[i][0] - prev;
            }
            return prev;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int y = 0; y < values.length; y++) {
                sb.append(Arrays.toString(values[y]).replaceAll("[\\[\\],]", ""));
                sb.append("\n");
            }
            return sb.toString();
        }

    }

    private List<Grid> grids;

    public Day09(String file) throws IOException {
        this.grids = AocUtils.fileAsLineStream(file).map(Grid::new).toList();
    }
    
    public long solution1() {
        return grids.stream().mapToLong(Grid::next).sum();
    }
    
    public long solution2() {
        return grids.stream().mapToLong(Grid::prev).sum();
    }

    public static void main(String[] args) throws IOException {
        var problem = new Day09("inputs/09");
        System.out.println("9.1: " + problem.solution1());
        System.out.println("9.2: " + problem.solution2());
    }

}
