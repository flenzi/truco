package f.l.truco.machine;

public enum State {
    INITIAL,
    PLAYER_1_TURN_1,
    PLAYER_2_TURN_1,
    PLAYER_1_TURN_2,
    PLAYER_2_TURN_2,
    PLAYER_1_TURN_3,
    PLAYER_2_TURN_3,
    COMPUTE_1,
    COMPUTE_2,
    COMPUTE_3,
    COMPUTE_NEXT_PLAYER_TURN_2,
    COMPUTE_NEXT_PLAYER_TURN_3,
    COMPUTE_WINNER,

    FINAL,

    PLAYER_1_DECISION_TURN_1_PLAYER_1,
    PLAYER_1_DECISION_TURN_2_PLAYER_1,
    PLAYER_1_DECISION_TURN_3_PLAYER_1,
    PLAYER_2_DECISION_TURN_1_PLAYER_1,
    PLAYER_2_DECISION_TURN_2_PLAYER_1,
    PLAYER_2_DECISION_TURN_3_PLAYER_1,

    PLAYER_1_DECISION_TURN_1_PLAYER_2,
    PLAYER_1_DECISION_TURN_2_PLAYER_2,
    PLAYER_1_DECISION_TURN_3_PLAYER_2,
    PLAYER_2_DECISION_TURN_1_PLAYER_2,
    PLAYER_2_DECISION_TURN_2_PLAYER_2,
    PLAYER_2_DECISION_TURN_3_PLAYER_2,

    NONE;

    public static State getNextStateAfterYesDecision(State currentState) {
        if (currentState == State.PLAYER_1_DECISION_TURN_1_PLAYER_1 || currentState == State.PLAYER_2_DECISION_TURN_1_PLAYER_1) {
            return State.PLAYER_1_TURN_1;
        } else if (currentState == State.PLAYER_1_DECISION_TURN_2_PLAYER_1 || currentState == State.PLAYER_2_DECISION_TURN_2_PLAYER_1) {
            return State.PLAYER_1_TURN_2;
        } else if (currentState == State.PLAYER_1_DECISION_TURN_3_PLAYER_1 || currentState == State.PLAYER_2_DECISION_TURN_3_PLAYER_1) {
            return State.PLAYER_1_TURN_3;
        } else if (currentState == State.PLAYER_1_DECISION_TURN_1_PLAYER_2 || currentState == State.PLAYER_2_DECISION_TURN_1_PLAYER_2) {
            return State.PLAYER_2_TURN_1;
        } else if (currentState == State.PLAYER_1_DECISION_TURN_2_PLAYER_2 || currentState == State.PLAYER_2_DECISION_TURN_2_PLAYER_2) {
            return State.PLAYER_2_TURN_2;
        } else { //(currentState == State.PLAYER_1_DECISION_TURN_3_PLAYER_2 || currentState == State.PLAYER_2_DECISION_TURN_3_PLAYER_2)
            return State.PLAYER_2_TURN_3;
        }
    }
}
