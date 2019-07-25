package f.l.truco;

import f.l.truco.model.Card;
import f.l.truco.model.Suit;
import java.util.ArrayList;
import java.util.List;

class TestUtils {

    static List<Card> getPlayer1Cards() {
        List<Card> player1Cards = new ArrayList<>();
        player1Cards.add(Card.get(Suit.SWORDS, 1));
        player1Cards.add(Card.get(Suit.SWORDS, 2));
        player1Cards.add(Card.get(Suit.SWORDS, 6));
        return player1Cards;
    }

    static List<Card> getPlayer2Cards() {
        List<Card> player2Cards = new ArrayList<>();
        player2Cards.add(Card.get(Suit.GOLDS, 3));
        player2Cards.add(Card.get(Suit.GOLDS, 4));
        player2Cards.add(Card.get(Suit.CLUBS, 1));
        return player2Cards;
    }

    static List<Card> getPlayer1DrawDrawDrawCards() {
        List<Card> player1Cards = new ArrayList<>();
        player1Cards.add(Card.get(Suit.SWORDS, 4));
        player1Cards.add(Card.get(Suit.SWORDS, 5));
        player1Cards.add(Card.get(Suit.SWORDS, 6));
        return player1Cards;
    }

    static List<Card> getPlayer2DrawDrawDrawCards() {
        List<Card> player2Cards = new ArrayList<>();
        player2Cards.add(Card.get(Suit.GOLDS, 4));
        player2Cards.add(Card.get(Suit.GOLDS, 5));
        player2Cards.add(Card.get(Suit.GOLDS, 6));
        return player2Cards;
    }

    static List<Card> getPlayer1DrawDrawWinCards() {
        List<Card> player1Cards = new ArrayList<>();
        player1Cards.add(Card.get(Suit.SWORDS, 4));
        player1Cards.add(Card.get(Suit.SWORDS, 5));
        player1Cards.add(Card.get(Suit.SWORDS, 1));
        return player1Cards;
    }

    static List<Card> getPlayer2DrawDrawWinCards() {
        List<Card> player2Cards = new ArrayList<>();
        player2Cards.add(Card.get(Suit.GOLDS, 4));
        player2Cards.add(Card.get(Suit.GOLDS, 5));
        player2Cards.add(Card.get(Suit.GOLDS, 1));
        return player2Cards;
    }

    static List<Card> getPlayer1DrawWinWinCards() {
        List<Card> player1Cards = new ArrayList<>();
        player1Cards.add(Card.get(Suit.SWORDS, 4));
        player1Cards.add(Card.get(Suit.SWORDS, 7));
        player1Cards.add(Card.get(Suit.SWORDS, 1));
        return player1Cards;
    }

    static List<Card> getPlayer2DrawWinWinCards() {
        List<Card> player2Cards = new ArrayList<>();
        player2Cards.add(Card.get(Suit.CLUBS, 4));
        player2Cards.add(Card.get(Suit.CLUBS, 5));
        player2Cards.add(Card.get(Suit.CLUBS, 1));
        return player2Cards;
    }
}
