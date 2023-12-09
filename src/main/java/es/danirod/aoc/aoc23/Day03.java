package es.danirod.aoc.aoc23;

import es.danirod.aoc.aoc23.support.GridFile;
import es.danirod.aoc.aoc23.support.Point;
import java.io.IOException;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.Collectors;

public class Day03 {

    private final GridFile file;

    public Day03(String path) throws IOException {
        this.file = AocUtils.gridFile(path);
    }

    private Stream<PartNumber> parts() {
        Stream.Builder<PartNumber> stream = Stream.builder();
        for (int y = 0; y < file.lines(); y++) {
            for (int x = 0; x < file.cols(); x++) {
                if (Character.isDigit(file.at(x, y))) {
                    var startX = x;
                    do {
                        x++;
                    } while (x < file.cols() && Character.isDigit(file.at(x, y)));
                    var endX = x - 1;

                    var partNumber = new PartNumber(y, startX, endX);
                    stream.add(partNumber);
                }
            }
        }
        return stream.build();
    }

    public Stream<Point> gears() {
        return file.cells().filter((cell) -> cell.value() == '*').map((cell) -> new Point(cell.x(), cell.y()));
    }

    public int solution1() {
        var aloneParts = parts().filter((partNumber) -> partNumber.surrounds(file).anyMatch((p) -> {
            char value = file.at(p);
            return value != '.' && !Character.isDigit(value);
        }));
        return aloneParts.mapToInt(part -> part.value(file)).sum();
    }

    public int solution2() {
        var gears = gears().collect(Collectors.toMap(
                Function.identity(),
                (gear) -> parts().filter((part) -> part.surrounds(file).anyMatch(p -> p.equals(gear))).toList()
        ));
        return gears.entrySet().stream()
                .filter(entry -> entry.getValue().size() >= 2)
                .map(entry -> entry.getValue())
                .mapToInt(parts -> parts.stream().mapToInt(p -> p.value(file)).reduce(1, (a, b) -> a * b))
                .sum();
    }

    public static void main(String[] args) throws IOException {
        var problem = new Day03("inputs/03");
        System.out.println("3.1: " + problem.solution1());
        System.out.println("3.2: " + problem.solution2());

    }

    public record PartNumber(int y, int startX, int endX) {

        public int value(GridFile file) {
            int acc = 0;
            for (int x = startX; x <= endX; x++) {
                var cell = file.at(x, y) - '0';
                acc = acc * 10 + cell;
            }
            return acc;
        }

        public Stream<Point> surrounds(GridFile file) {
            var points = IntStream.rangeClosed(y - 1, y + 1)
                    .mapToObj(y -> IntStream
                    .rangeClosed(startX - 1, endX + 1)
                    .mapToObj(x -> new Point(x, y)));
            var everything = points.reduce(Stream.empty(), (prev, next) -> Stream.concat(prev, next));
            return everything
                    .filter((point) -> {
                        // remove points out of the bounds
                        if (point.x() < 0 || point.y() < 0 || point.x() >= file.cols() || point.y() >= file.lines()) {
                            return false;
                        }
                        return !(point.y() == y && point.x() >= startX && point.x() <= endX);
                    });
        }
    }
}
