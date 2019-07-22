package f.l.truco.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum Suit {
    SWORDS,
    CLUBS,
    GOLDS,
    CUPS;

    private static final List<Suit> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private static Random random = new Random();

    public static Suit getRandom() {
        return VALUES.get(random.nextInt(Suit.SIZE));
    }
}
