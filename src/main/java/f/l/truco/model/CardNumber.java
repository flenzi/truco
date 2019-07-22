package f.l.truco.model;

import java.util.Random;

public class CardNumber {

    private static final int[] possibleValues = {1, 2, 3, 4, 5, 6, 7, 10, 11, 12};

    private static Random random = new Random();

    public static int getRandom() {
        return possibleValues[random.nextInt(possibleValues.length)];
    }
}
