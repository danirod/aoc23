package es.danirod.aoc.aoc23;

import es.danirod.aoc.aoc23.support.GridFile;
import es.danirod.aoc.aoc23.support.Point;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Day11 {
    
    private GridFile photo;
    
    private List<Point> galaxies;
    
    public Day11(String file) throws IOException {
        photo = new GridFile(AocUtils.fileAsLines(file));
        galaxies = photo.cells()
                .filter(cell -> cell.value() == '#')
                .map(cell -> cell.point())
                .toList();
    }
    
    private long solve(SparsePhoto sparse) {
        List<Point> reals = galaxies.stream().map(sparse::virtual).toList();
        long total = 0;
        for (int i = 0; i < reals.size(); i++) {
            for (int j = i + 1; j < reals.size(); j++) {
                total += reals.get(i).taxicab(reals.get(j));
            }
        }
        return total;
    }
    
    public long solution1() {
        return solve(new SparsePhoto(2));
    }
    
    public long solution2() {
        return solve(new SparsePhoto(1000000));
    }
            
    public static void main(String[] args) throws IOException {
        var problem = new Day11("inputs/11");
        System.out.println("11.1: " + problem.solution1());
        System.out.println("11.2: " + problem.solution2());
    }
    
    class SparsePhoto {
        
        private int[] virtualLines = new int[photo.lines()];
        
        private int[] virtualColumns = new int[photo.cols()];
        
        public SparsePhoto(int spacing) {
            boolean[] emptyLines = new boolean[photo.lines()];
            boolean[] emptyCols = new boolean[photo.cols()];
                    
            // First annotate which columns and lines are actually empty.
            Arrays.fill(emptyLines, true);
            Arrays.fill(emptyCols, true);
            for (int y = 0; y < emptyLines.length; y++) {
                for (int x = 0; x < emptyCols.length; x++) {
                    if (photo.at(x, y) == '#') {
                        emptyLines[y] = false;
                        emptyCols[x] = false;
                    }
                }
            }
            
            // Then compute the virtual indices after spacing.
            for (int a = 0, y = 0; y < virtualLines.length; y++) {
                if (emptyLines[y]) a += (spacing - 1);
                virtualLines[y] = a++;
            }
            for (int a = 0, x = 0; x < virtualColumns.length; x++) {
                if (emptyCols[x]) a += (spacing - 1);
                virtualColumns[x] = a++;
            }
        }
        
        public Point virtual(Point p) {
            return new Point(virtualColumns[p.x()], virtualLines[p.y()]);
        }
    }
}
