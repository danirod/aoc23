package es.danirod.aoc.aoc23;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.LongStream;

public class Day05 {

    public record Range(long destination, long source, long length) {

        public static Range build(String line) {
            var record = line.strip().split(" ");
            return new Range(
                    Long.parseLong(record[0]),
                    Long.parseLong(record[1]),
                    Long.parseLong(record[2])
            );
        }

        /**
         * How much you have to add (or subtract, if negative) to get
         * destination.
         */
        public long offset() {
            return destination - source;
        }

        public long start() {
            return source;
        }

        public long end() {
            return source + length;
        }
    }

    public record LookupTable(List<Range> records) {

        public long value(long source) {
            for (Range r : records) {
                // Found if the source value falls inside source() and length()
                if (source >= r.start() && source < r.end()) {
                    return source + r.offset();
                }

                // Since records are sorted, it is so over
                if (source < r.source()) {
                    break;
                }
            }
            return source;
        }
    }

    public record Almanac(long[] seeds, List<LookupTable> tables) {

        public long convert(long source) {
            long value = source;
            for (LookupTable lt : tables) {
                value = lt.value(value);
            }
            return value;
        }

    }

    private String path;

    public Day05(String path) {
        this.path = path;
    }

    private long[] seeds(String line) {
        var tokens = line.strip().split(" ");
        var seeds = new long[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            seeds[i] = Long.parseLong(tokens[i]);
        }
        return seeds;
    }

    private Almanac parse() throws IOException {
        var lines = AocUtils.fileAsLines(path);
        var tables = new ArrayList<LookupTable>();
        var seeds = this.seeds(lines.get(0).replace("seeds: ", ""));
        List<Range> current = null;
        for (int i = 1; i < lines.size(); i++) {
            if (lines.get(i).isBlank()) {
                // Next line contains our new current thing.
                if (current != null) {
                    // Seems that there is no overlap between ranges.
                    current.sort((a, b) -> Long.compare(a.source, b.source));
                    tables.add(new LookupTable(current));
                }
                current = new ArrayList<>();
                i++; // skip the element already
            } else {
                var range = Range.build(lines.get(i));
                current.add(range);
            }
        }
        // Remember to add the last line.
        current.sort((a, b) -> Long.compare(a.source, b.source));
        tables.add(new LookupTable(current));

        return new Almanac(seeds, tables);
    }

    private LongStream seedRanges(long[] seeds) {
        var stream = LongStream.empty();
        for (int i = 0; i < seeds.length; i += 2) {
            var start = seeds[i];
            var length = seeds[i + 1];
            var next = LongStream.range(start, start + length);
            stream = LongStream.concat(stream, next);
        }
        return stream;
    }

    public long solution1() throws IOException {
        var almanac = parse();
        return Arrays.stream(almanac.seeds)
                .map(almanac::convert)
                .min()
                .getAsLong();
    }

    public long solution2() throws IOException {
        var almanac = parse();

        return seedRanges(almanac.seeds)
                .parallel()
                .map(almanac::convert)
                .reduce(Long.MAX_VALUE, (a, b) -> a < b ? a : b);
    }

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        var problem = new Day05("inputs/05");
        System.out.println("5.1: " + problem.solution1());
        System.out.println("5.2: " + problem.solution2());
    }
}
