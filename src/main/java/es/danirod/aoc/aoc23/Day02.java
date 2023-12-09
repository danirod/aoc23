package es.danirod.aoc.aoc23;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day02 {

    public static record CubeSet(Map<String, Integer> cubes) {

        public static CubeSet build(String query) {
            var result = Arrays.stream(query.split(","))
                    .map(entry -> entry.strip().split(" "))
                    .collect(Collectors.toMap(
                            arr -> arr[1],
                            arr -> Integer.valueOf(arr[0])
                    ));
            return new CubeSet(result);
        }

        public int count(String key) {
            return cubes().getOrDefault(key, 0);
        }
    }

    public static record Game(int id, List<CubeSet> sets) {

        public static Game parse(String query) {
            var pattern = Pattern.compile("Game (\\d+): (.*)$");
            var matcher = pattern.matcher(query);
            if (matcher.find()) {
                var id = Integer.valueOf(matcher.group(1));
                var cubes = matcher.group(2);
                var sets = Arrays.stream(cubes.split("; ")).map(CubeSet::build).toList();
                return new Game(id, sets);
            }
            throw new RuntimeException("Unsupported query: " + query);
        }

        public int min(String key) {
            ToIntFunction<CubeSet> cubesOfType = (CubeSet cs) -> cs.count(key);
            return sets().stream().mapToInt(cubesOfType).max().getAsInt();
        }

        public int red() {
            return min("red");
        }

        public int green() {
            return min("green");
        }

        public int blue() {
            return min("blue");
        }

        public int power() {
            return red() * blue() * green();
        }
    }

    private static final String PATH = "inputs/02";

    public long solution1() throws IOException {
        var lines = AocUtils.fileAsLineStream(PATH);
        var total = lines
                .map(Game::parse)
                .filter(game -> game.min("red") <= 12 && game.min("green") <= 13 && game.min("blue") <= 14)
                .mapToInt(Game::id)
                .sum();
        return total;
    }

    public long solution2() throws IOException {
        var lines = AocUtils.fileAsLineStream(PATH);
        var total = lines
                .map(Game::parse)
                .mapToInt(Game::power)
                .sum();
        return total;
    }

    public static void main(String[] args) throws IOException {
        System.out.println("2.1: " + new Day02().solution1());
        System.out.println("2.2: " + new Day02().solution2());
    }

}
