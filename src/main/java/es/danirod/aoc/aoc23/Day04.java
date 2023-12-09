package es.danirod.aoc.aoc23;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day04 {

    final String file;

    public Day04(String file) {
        this.file = file;
    }

    public int solution1() throws IOException {
        return AocUtils.fileAsLineStream(file)
                .map(Card::build)
                .mapToInt(Card::score)
                .sum();
    }

    public int solution2() throws IOException {
        Map<Integer, Card> cards = AocUtils
                .fileAsLineStream(file)
                .map(Card::build)
                .collect(Collectors.toMap(Card::id, card -> card));
        Map<Integer, Integer> counts = cards
                .keySet()
                .stream()
                .collect(Collectors.toMap(id -> id, _id -> 1));

        for (int i = 1; i <= cards.size(); i++) {
            int winners = cards.get(i).countWinners();
            int myCards = counts.get(i);
            for (int w = 1; w <= winners; w++) {
                if (i + w > cards.size()) {
                    break;
                }
                counts.put(i + w, counts.get(i + w) + myCards);
            }
        }
        return counts.values().stream().mapToInt(i -> i).sum();
    }

    public static void main(String[] args) throws IOException {
        var problem = new Day04("inputs/04");
        System.out.println("4.1: " + problem.solution1());
        System.out.println("4.2: " + problem.solution2());
    }

    public record Card(int id, Set<Integer> numbers, Set<Integer> winners) {

        private static Set<Integer> numbers(String str) {
            return Arrays.stream(str.strip().split("\\s+"))
                    .map(Integer::valueOf)
                    .collect(Collectors.toSet());
        }

        public static Card build(String line) {
            var mainMatch = Pattern.compile("Card\\s+(\\d+): ([\\d ]+)\\| ([\\d ]+)");
            var matcher = mainMatch.matcher(line);
            if (matcher.find()) {
                int id = Integer.parseInt(matcher.group(1));
                Set<Integer> numbers = numbers(matcher.group(2));
                Set<Integer> winners = numbers(matcher.group(3));
                return new Card(id, numbers, winners);
            }
            throw new IllegalArgumentException("Does not match: " + line);
        }

        public int countWinners() {
            Set<Integer> intersection = new HashSet<>(numbers);
            intersection.retainAll(winners);
            return intersection.size();
        }

        public int score() {
            int ws = countWinners();
            int score = 0;
            for (int w = 0; w < ws; w++) {
                if (score == 0) {
                    score = 1;
                } else {
                    score *= 2;
                }
            }
            return score;
        }

    }
}
