package es.danirod.aoc.aoc23;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import es.danirod.aoc.aoc23.Day07.Hand;
import es.danirod.aoc.aoc23.Day07.HandType;

public class Day07Test {
    
    @Test
    public void testHandType() {
        // Remember that it returns positive if the left is stronger than the right.
        assertTrue(HandType.compare(HandType.FiveOfKind, HandType.FourOfKind) > 0);
        assertTrue(HandType.compare(HandType.TwoPair, HandType.TwoPair) == 0);
        assertTrue(HandType.compare(HandType.OnePair, HandType.FullHouse) < 0);
    }
    
    @Test
    public void testHands() {
        // Remember that it returns positive if the left is stronger than the right.
        
        // Some examples with different hand types.
        assertTrue(Hand.compare(new Hand("AAAAA"), new Hand("AAQQB")) > 0);
        assertTrue(Hand.compare(new Hand("98777"), new Hand("98777")) == 0);
        assertTrue(Hand.compare(new Hand("2233F"), new Hand("22233")) < 0);
        
        // Some examples where the hand type is the same and compare by position.
        assertTrue(Hand.compare(new Hand("33332"), new Hand("2AAAA")) > 0);
        assertTrue(Hand.compare(new Hand("22211"), new Hand("22211")) == 0);
        assertTrue(Hand.compare(new Hand("77788"), new Hand("77888")) < 0);
    }
    
}
