package f.l.truco;

import f.l.truco.model.Card;
import f.l.truco.model.Suit;

import java.util.Arrays;
import java.util.List;

class TestUtils {

    static List<Card> getPlayer1Cards() {
        return Arrays.asList(Card.get(Suit.SWORDS, 1), Card.get(Suit.SWORDS, 2), Card.get(Suit.SWORDS, 6));
    }

    static List<Card> getPlayer2Cards() {
        return Arrays.asList(Card.get(Suit.GOLDS, 3), Card.get(Suit.GOLDS, 4), Card.get(Suit.CLUBS, 1));
    }

    static List<Card> getPlayer1DrawDrawDrawCards() {
        return Arrays.asList(Card.get(Suit.SWORDS, 4), Card.get(Suit.SWORDS, 5), Card.get(Suit.SWORDS, 6));
    }

    static List<Card> getPlayer2DrawDrawDrawCards() {
        return Arrays.asList(Card.get(Suit.GOLDS, 4), Card.get(Suit.GOLDS, 5), Card.get(Suit.GOLDS, 6));
    }

    static List<Card> getPlayer1DrawDrawWinCards() {
        return Arrays.asList(Card.get(Suit.SWORDS, 4), Card.get(Suit.SWORDS, 5), Card.get(Suit.SWORDS, 1));
    }

    static List<Card> getPlayer2DrawDrawWinCards() {
        return Arrays.asList(Card.get(Suit.GOLDS, 4), Card.get(Suit.GOLDS, 5), Card.get(Suit.GOLDS, 1));
    }

    static List<Card> getPlayer1DrawWinWinCards() {
        return Arrays.asList(Card.get(Suit.SWORDS, 4), Card.get(Suit.SWORDS, 7), Card.get(Suit.SWORDS, 1));
    }

    static List<Card> getPlayer2DrawWinWinCards() {
        return Arrays.asList(Card.get(Suit.CLUBS, 4), Card.get(Suit.CLUBS, 5), Card.get(Suit.CLUBS, 1));
    }
}
