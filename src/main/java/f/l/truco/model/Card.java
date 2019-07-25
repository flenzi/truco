package f.l.truco.model;

import static f.l.truco.model.Suit.CLUBS;
import static f.l.truco.model.Suit.CUPS;
import static f.l.truco.model.Suit.GOLDS;
import static f.l.truco.model.Suit.SWORDS;

import java.util.Objects;

public final class Card implements Comparable<Card> {

    private final Suit suit;
    private final int number;

    public Card(Suit suit, int number) {
        this.suit = suit;
        this.number = number;
    }

    public static Card generateRandomCard() {
        return new Card(Suit.getRandom(), CardNumber.getRandom());
    }

    public static Card get(Suit clubs, int i) {
        return new Card(clubs, i);
    }

    public Suit getSuit() {
        return suit;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Card card = (Card) o;
        return number == card.number &&
                suit == card.suit;
    }

    @Override
    public String toString() {
        return "Card{" +
                "suit=" + suit +
                ", number=" + number +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(suit, number);
    }

    @Override
    public int compareTo(Card o) {
        return this.getCardWeight().compareTo(o.getCardWeight());
    }

    private Integer getCardWeight() {
        if (suit == SWORDS && number == 1) {
            return 10;
        } else if (suit == CLUBS && number == 1) {
            return 9;
        } else if (suit == SWORDS && number == 7) {
            return 8;
        } else if (suit == GOLDS && number == 7) {
            return 7;
        } else if (number == 3) {
            return 6;
        } else if (number == 2) {
            return 5;
        } else if ((suit == GOLDS || suit == CUPS) && number == 1) {
            return 4;
        } else if ((suit == CLUBS || suit == CUPS) && number == 7) {
            return 3;
        } else if (number == 6) {
            return 2;
        } else if (number == 5) {
            return 1;
        } else { //number == 4
            return 0;
        }
    }
}
