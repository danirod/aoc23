package es.danirod.aoc.aoc23.support;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class GridFile {

    public static record Cell(int x, int y, char value) {

    }

    private final char[][] chars;

    public GridFile(List<String> lines) throws IOException {
        this.chars = lines.stream().map(String::toCharArray).toArray(char[][]::new);
        for (char[] line : chars) {
            if (line.length != chars[0].length) {
                throw new IllegalArgumentException("File grid is not square");
            }
        }

    }

    public int lines() {
        return chars.length;
    }

    public int cols() {
        return chars[0].length;
    }

    public char at(int x, int y) {
        return chars[y][x];
    }

    public char at(Point p) {
        return at(p.x(), p.y());
    }

    public boolean inside(int x, int y) {
        return x >= 0 && y >= 0 && x < cols() && y < lines();
    }

    public boolean inside(Point p) {
        return inside(p.x(), p.y());
    }

    public char[] row(int y) {
        return chars[y];
    }

    public Stream<Point> points() {
        return IntStream.range(0, lines())
                .mapToObj(y -> IntStream.range(0, cols()).mapToObj(x -> new Point(x, y)))
                .reduce(Stream.empty(), (prev, next) -> Stream.concat(prev, next));
    }

    public Stream<Cell> cells() {
        return IntStream.range(0, lines())
                .mapToObj(y -> IntStream.range(0, cols()).mapToObj(x -> new Cell(x, y, at(x, y))))
                .reduce(Stream.empty(), (prev, next) -> Stream.concat(prev, next));
    }
}
