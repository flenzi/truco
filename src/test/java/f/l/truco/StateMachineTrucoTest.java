package f.l.truco;

import f.l.truco.machine.Event;
import f.l.truco.model.Card;
import f.l.truco.model.Suit;
import org.junit.Test;
import org.springframework.messaging.support.MessageBuilder;

import static f.l.truco.TestUtils.getPlayer1Cards;
import static f.l.truco.TestUtils.getPlayer2Cards;
import static f.l.truco.machine.ExtendedStateVariable.*;
import static f.l.truco.machine.MessageHeader.PLAYED_CARD;
import static f.l.truco.machine.State.*;
import static f.l.truco.model.Player.PLAYER_1;
import static f.l.truco.model.Player.PLAYER_2;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StateMachineTrucoTest extends StateMachineCommonTests {

    @Test
    public void player1Truco_player2No_test() {
        assertTrue(stateMachine.sendEvent(MessageBuilder.withPayload(Event.INITIALIZE_TEST)
                .setHeader("player1cards", getPlayer1Cards())
                .setHeader("player2cards", getPlayer2Cards())
                .build()));

        assertEquals(PLAYER_1_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_1_TRUCO)
                .build());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_2_NO_TRUCO)
                .build());

        assertEquals(FINAL, stateMachine.getState().getId());
        assertEquals(PLAYER_1, stateMachine.getExtendedState().getVariables().get(WINNER));
        assertEquals(1, stateMachine.getExtendedState().getVariables().get(PLAYER_1_SCORE));
        assertEquals(0, stateMachine.getExtendedState().getVariables().get(PLAYER_2_SCORE));
    }

    @Test
    public void player2Truco_player1No_test() {
        assertTrue(stateMachine.sendEvent(MessageBuilder.withPayload(Event.INITIALIZE_TEST)
                .setHeader("player1cards", getPlayer1Cards())
                .setHeader("player2cards", getPlayer2Cards())
                .build()));

        assertEquals(PLAYER_1_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_2_TRUCO)
                .build());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_1_NO_TRUCO)
                .build());

        assertEquals(FINAL, stateMachine.getState().getId());
        assertEquals(PLAYER_2, stateMachine.getExtendedState().getVariables().get(WINNER));
        assertEquals(0, stateMachine.getExtendedState().getVariables().get(PLAYER_1_SCORE));
        assertEquals(1, stateMachine.getExtendedState().getVariables().get(PLAYER_2_SCORE));
    }

    @Test
    public void player1Truco_player2Yes_retruco_No_test() {
        assertTrue(stateMachine.sendEvent(MessageBuilder.withPayload(Event.INITIALIZE_TEST)
                .setHeader("player1cards", getPlayer1Cards())
                .setHeader("player2cards", getPlayer2Cards())
                .build()));

        assertEquals(PLAYER_1_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_1_TRUCO)
                .build());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_2_YES_TRUCO)
                .build());

        assertEquals(PLAYER_1_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_2_RE_TRUCO)
                .build());
        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_1_NO_RETRUCO)
                .build());
        assertEquals(FINAL, stateMachine.getState().getId());
        assertEquals(PLAYER_2, stateMachine.getExtendedState().getVariables().get(WINNER));
        assertEquals(0, stateMachine.getExtendedState().getVariables().get(PLAYER_1_SCORE));
        assertEquals(2, stateMachine.getExtendedState().getVariables().get(PLAYER_2_SCORE));
    }

    @Test
    public void player1Truco_player2Yes_retruco_yes_vale4_no_test() {
        assertTrue(stateMachine.sendEvent(MessageBuilder.withPayload(Event.INITIALIZE_TEST)
                .setHeader("player1cards", getPlayer1Cards())
                .setHeader("player2cards", getPlayer2Cards())
                .build()));

        assertEquals(PLAYER_1_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_1_TRUCO)
                .build());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_2_YES_TRUCO)
                .build());

        assertEquals(PLAYER_1_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_2_RE_TRUCO)
                .build());
        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_1_YES_RETRUCO)
                .build());
        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_1_VALE_4)
                .build());
        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_2_NO_VALE4)
                .build());
        assertEquals(FINAL, stateMachine.getState().getId());
        assertEquals(PLAYER_1, stateMachine.getExtendedState().getVariables().get(WINNER));
        assertEquals(3, stateMachine.getExtendedState().getVariables().get(PLAYER_1_SCORE));
        assertEquals(0, stateMachine.getExtendedState().getVariables().get(PLAYER_2_SCORE));
    }

    @Test
    public void player1Truco_player2Yes_retruco_yes_test() {
        assertTrue(stateMachine.sendEvent(MessageBuilder.withPayload(Event.INITIALIZE_TEST)
                .setHeader("player1cards", getPlayer1Cards())
                .setHeader("player2cards", getPlayer2Cards())
                .build()));

        assertEquals(PLAYER_1_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_1_TRUCO)
                .build());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_2_YES_TRUCO)
                .build());

        assertEquals(PLAYER_1_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_2_RE_TRUCO)
                .build());
        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_1_YES_RETRUCO)
                .build());
        assertEquals(PLAYER_1_TURN_1, stateMachine.getState().getId());

        makePlayer1Win();

        assertEquals(FINAL, stateMachine.getState().getId());
        assertEquals(PLAYER_1, stateMachine.getExtendedState().getVariables().get(WINNER));
        assertEquals(3, stateMachine.getExtendedState().getVariables().get(PLAYER_1_SCORE));
        assertEquals(0, stateMachine.getExtendedState().getVariables().get(PLAYER_2_SCORE));
    }

    @Test
    public void player1Truco_player2Yes_retruco_yes_vale4_yes_test() {
        assertTrue(stateMachine.sendEvent(MessageBuilder.withPayload(Event.INITIALIZE_TEST)
                .setHeader("player1cards", getPlayer1Cards())
                .setHeader("player2cards", getPlayer2Cards())
                .build()));

        assertEquals(PLAYER_1_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_1_TRUCO)
                .build());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_2_YES_TRUCO)
                .build());

        assertEquals(PLAYER_1_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_2_RE_TRUCO)
                .build());
        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_1_YES_RETRUCO)
                .build());
        assertEquals(PLAYER_1_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_1_VALE_4)
                .build());
        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_2_YES_VALE4)
                .build());
        assertEquals(PLAYER_1_TURN_1, stateMachine.getState().getId());

        makePlayer1Win();

        assertEquals(FINAL, stateMachine.getState().getId());
        assertEquals(PLAYER_1, stateMachine.getExtendedState().getVariables().get(WINNER));
        assertEquals(4, stateMachine.getExtendedState().getVariables().get(PLAYER_1_SCORE));
        assertEquals(0, stateMachine.getExtendedState().getVariables().get(PLAYER_2_SCORE));
    }

    private void makePlayer1Win() {
        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_1_PLAY_CARD_1)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 1))
                .build());

        assertEquals(PLAYER_2_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_2_PLAY_CARD_1)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.CLUBS, 1))
                .build());

        assertEquals(PLAYER_1_TURN_2, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_1_PLAY_CARD_2)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 2))
                .build());

        assertEquals(PLAYER_2_TURN_2, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_2_PLAY_CARD_2)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.GOLDS, 4))
                .build());
    }
}
