package es.danirod.aoc.aoc23;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/*
 * Some important internal notes on comparators.
 *
 * Comparators are given two objects of same kind (a, b) and return a number:
 * - return a negative number if a is less than b.
 * - return a positive number if a is greater than b.
 * - return zero if a is equal to b.
 *
 * Through this problem, I am using comparators to assert the strength.
 * For instance, the strength of a HandType is defined by its order.
 * - compare(FullHouse, TwoPair), a is greater than b so I return a positive.
 * - compare(ThreePair, ThreePair), a equals b so I return zero.
 * - compare(OnePair, FiveOfKind), a is less than b so I return a negative.
 *
 * However, many times I'll be comparing raw integers. Each HandType is given
 * a order, which grows as the hand type gets more important. FullHouse has
 * order 6 and TwoPair has order 2, FullHouse > TwoPair, therefore 6 > 2.
 * Makes sense.
 */
public class Day07 {

    private static String CARD_ORDER = "23456789TJQKA";

    private static String JOKER_CARD_ORDER = "J23456789TQKA";

    public static enum HandType {
        FiveOfKind(6), // 5 (distinct = 1)
        FourOfKind(5), // 4 + 1 (distinct = 2)
        FullHouse(4), // 3 + 2 (distinct = 2)
        ThreeOfKind(3), // 3 + 1 + 1 (distinct = 3)
        TwoPair(2), // 2 + 2 + 1 (distinct = 3)
        OnePair(1), // 2 + 1 + 1 + 1 (distinct = 4)
        HighCard(0); // 1 + 1 + 1 + 1 + 1 (distinct = 5)

        HandType(int order) {
            this.order = order;
        }

        final int order;

        public static int compare(HandType h1, HandType h2) {
            return Integer.compare(h1.order, h2.order);
        }
    }

    public record Hand(String values) {

        public HandType type() {
            Map<Character, Integer> counts = new TreeMap<>();
            for (char ch : values.toCharArray()) {
                var value = counts.getOrDefault(ch, 0);
                counts.put(ch, value + 1);
            }
            Collection<Integer> groups = counts.values();

            return switch (counts.size()) {
                case 1 ->
                    HandType.FiveOfKind;
                case 2 ->
                    groups.contains(4) ? HandType.FourOfKind : HandType.FullHouse;
                case 3 ->
                    groups.contains(3) ? HandType.ThreeOfKind : HandType.TwoPair;
                case 4 ->
                    HandType.OnePair;
                case 5 ->
                    HandType.HighCard;
                default ->
                    null;
            };
        }

        public HandType jokerType() {
            // If no jokers, then delegate to the original type().
            if (values.indexOf('J') == -1) {
                return type();
            }

            char highLetter = 0;
            int highCount = 0;

            Map<Character, Integer> counts = new TreeMap<>();
            for (char ch : values.toCharArray()) {
                var value = counts.getOrDefault(ch, 0);
                counts.put(ch, value + 1);
                if (ch != 'J' && value + 1 > highCount) {
                    highCount = value + 1;
                    highLetter = ch;
                }
            }

            Hand updated = new Hand(values.replaceAll("J", String.valueOf(highLetter)));
            return updated.type();
        }

        public static int compare(Hand h1, Hand h2) {
            // Compare by type first, which is the first rule.
            HandType type1 = h1.type(), type2 = h2.type();
            if (type1 != type2) {
                return HandType.compare(type1, type2);
            }

            // Types matches, so second guess is by lexical order.
            for (int i = 0; i < 5; i++) {
                int l1 = h1.values.charAt(i), l2 = h2.values.charAt(i);
                int i1 = CARD_ORDER.indexOf(l1), i2 = CARD_ORDER.indexOf(l2);
                if (i1 != i2) {
                    return Integer.compare(i1, i2);
                }
            }
            return 0;
        }

        public static int jokerCompare(Hand h1, Hand h2) {
            // Compare by type first, which is the first rule.
            HandType type1 = h1.jokerType(), type2 = h2.jokerType();
            if (type1 != type2) {
                return HandType.compare(type1, type2);
            }

            // Types matches, so second guess is by lexical order.
            for (int i = 0; i < 5; i++) {
                int l1 = h1.values.charAt(i), l2 = h2.values.charAt(i);
                int i1 = JOKER_CARD_ORDER.indexOf(l1), i2 = JOKER_CARD_ORDER.indexOf(l2);
                if (i1 != i2) {
                    return Integer.compare(i1, i2);
                }
            }
            return 0;
        }
    }

    public record Bid(Hand hand, int amount) {

    }

    private List<Bid> bids;

    public Day07(String file) throws IOException {
        this.bids = AocUtils.fileAsLineStream(file)
                .map(line -> {
                    var tokens = line.split(" ");
                    return new Bid(new Hand(tokens[0]), Integer.parseInt(tokens[1]));
                })
                .toList();
    }

    public int solution1() {
        List<Bid> sortedBids = bids.stream().sorted((b1, b2) -> Hand.compare(b1.hand, b2.hand)).toList();
        int i = 1;
        int ac = 0;
        for (Bid b : sortedBids) {
            ac += (i++ * b.amount);
        }
        return ac;
    }

    public int solution2() {
        List<Bid> sortedBids = bids.stream().sorted((b1, b2) -> Hand.jokerCompare(b1.hand, b2.hand)).toList();
        int i = 1;
        int ac = 0;
        for (Bid b : sortedBids) {
            ac += (i++ * b.amount);
        }
        return ac;
    }

    public static void main(String[] args) throws IOException {
        var problem = new Day07("inputs/07");
        System.out.println("7.1: " + problem.solution1());
        System.out.println("7.2: " + problem.solution2());
    }
}
