package f.l.truco;

import f.l.truco.machine.Event;
import f.l.truco.model.Card;
import f.l.truco.model.Suit;
import org.junit.Test;
import org.springframework.messaging.support.MessageBuilder;

import static f.l.truco.TestUtils.*;
import static f.l.truco.machine.ExtendedStateVariable.*;
import static f.l.truco.machine.MessageHeader.PLAYED_CARD;
import static f.l.truco.machine.State.*;
import static f.l.truco.model.Player.PLAYER_1;
import static f.l.truco.model.Player.PLAYER_2;
import static org.junit.Assert.*;

public class StateMachineRoundTest extends StateMachineCommonTests {

    @Test
    public void player1Win_player1Win_test() {
        assertTrue(stateMachine.sendEvent(MessageBuilder.withPayload(Event.INITIALIZE_TEST)
                .setHeader("player1cards", getPlayer1Cards())
                .setHeader("player2cards", getPlayer2Cards())
                .build()));

        assertEquals(PLAYER_1_TURN_1, stateMachine.getState().getId());

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

        assertEquals(FINAL, stateMachine.getState().getId());
        assertEquals(PLAYER_1, stateMachine.getExtendedState().getVariables().get(WINNER));
        assertEquals(1, stateMachine.getExtendedState().getVariables().get(PLAYER_1_SCORE));
        assertEquals(0, stateMachine.getExtendedState().getVariables().get(PLAYER_2_SCORE));
    }

    @Test
    public void player2Win_player2Win_test() {
        assertTrue(stateMachine.sendEvent(MessageBuilder.withPayload(Event.INITIALIZE_TEST)
                .setHeader("player1cards", getPlayer1Cards())
                .setHeader("player2cards", getPlayer2Cards())
                .build()));

        assertEquals(PLAYER_1_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_1_PLAY_CARD_1)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 2))
                .build());

        assertEquals(PLAYER_2_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_2_PLAY_CARD_1)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.CLUBS, 1))
                .build());

        assertEquals(PLAYER_2_TURN_2, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_2_PLAY_CARD_2)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.GOLDS, 3))
                .build());

        assertEquals(PLAYER_1_TURN_2, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_1_PLAY_CARD_2)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 6))
                .build());

        assertEquals(FINAL, stateMachine.getState().getId());
        assertEquals(PLAYER_2, stateMachine.getExtendedState().getVariables().get(WINNER));
        assertEquals(0, stateMachine.getExtendedState().getVariables().get(PLAYER_1_SCORE));
        assertEquals(1, stateMachine.getExtendedState().getVariables().get(PLAYER_2_SCORE));
    }

    @Test
    public void player2Win_player1Win_player_1Win_test() {
        assertTrue(stateMachine.sendEvent(MessageBuilder.withPayload(Event.INITIALIZE_TEST)
                .setHeader("player1cards", getPlayer1Cards())
                .setHeader("player2cards", getPlayer2Cards())
                .build()));

        assertEquals(PLAYER_1_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_1_PLAY_CARD_1)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 6))
                .build());

        assertEquals(PLAYER_2_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_2_PLAY_CARD_1)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.CLUBS, 1))
                .build());

        assertEquals(PLAYER_2_TURN_2, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_2_PLAY_CARD_2)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.GOLDS, 3))
                .build());

        assertEquals(PLAYER_1_TURN_2, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_1_PLAY_CARD_2)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 1))
                .build());

        assertEquals(PLAYER_1_TURN_3, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_1_PLAY_CARD_3)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 2)).build());

        assertEquals(PLAYER_2_TURN_3, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_2_PLAY_CARD_3)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.GOLDS, 4))
                .build());

        assertEquals(FINAL, stateMachine.getState().getId());
        assertEquals(PLAYER_1, stateMachine.getExtendedState().getVariables().get(WINNER));
        assertEquals(1, stateMachine.getExtendedState().getVariables().get(PLAYER_1_SCORE));
        assertEquals(0, stateMachine.getExtendedState().getVariables().get(PLAYER_2_SCORE));
    }

    @Test
    public void player1Win_player2Win_player_1Win_test() {
        assertTrue(stateMachine.sendEvent(MessageBuilder.withPayload(Event.INITIALIZE_TEST)
                .setHeader("player1cards", getPlayer1Cards())
                .setHeader("player2cards", getPlayer2Cards())
                .build()));

        assertEquals(PLAYER_1_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_1_PLAY_CARD_1)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 1))
                .build());

        assertEquals(PLAYER_2_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_2_PLAY_CARD_1)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.CLUBS, 1))
                .build());

        assertEquals(PLAYER_1_TURN_2, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_1_PLAY_CARD_2)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 6))
                .build());

        assertEquals(PLAYER_2_TURN_2, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_2_PLAY_CARD_2)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.GOLDS, 3))
                .build());

        assertEquals(PLAYER_2_TURN_3, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_2_PLAY_CARD_3)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.GOLDS, 4))
                .build());

        assertEquals(PLAYER_1_TURN_3, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_1_PLAY_CARD_3)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 2))
                .build());

        assertEquals(FINAL, stateMachine.getState().getId());
        assertEquals(PLAYER_1, stateMachine.getExtendedState().getVariables().get(WINNER));
        assertEquals(1, stateMachine.getExtendedState().getVariables().get(PLAYER_1_SCORE));
        assertEquals(0, stateMachine.getExtendedState().getVariables().get(PLAYER_2_SCORE));
    }

    @Test
    public void player1Win_player2Win_player_2Win_test() {
        assertTrue(stateMachine.sendEvent(MessageBuilder.withPayload(Event.INITIALIZE_TEST)
                .setHeader("player1cards", getPlayer1Cards())
                .setHeader("player2cards", getPlayer2Cards())
                .build()));

        assertEquals(PLAYER_1_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_1_PLAY_CARD_1)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 1))
                .build());

        assertEquals(PLAYER_2_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_2_PLAY_CARD_1)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.GOLDS, 4))
                .build());

        assertEquals(PLAYER_1_TURN_2, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_1_PLAY_CARD_2)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 2))
                .build());

        assertEquals(PLAYER_2_TURN_2, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_2_PLAY_CARD_2)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.CLUBS, 1))
                .build());

        assertEquals(PLAYER_2_TURN_3, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_2_PLAY_CARD_3)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.GOLDS, 3))
                .build());

        assertEquals(PLAYER_1_TURN_3, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_1_PLAY_CARD_3)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 6))
                .build());

        assertEquals(FINAL, stateMachine.getState().getId());
        assertEquals(PLAYER_2, stateMachine.getExtendedState().getVariables().get(WINNER));
        assertEquals(0, stateMachine.getExtendedState().getVariables().get(PLAYER_1_SCORE));
        assertEquals(1, stateMachine.getExtendedState().getVariables().get(PLAYER_2_SCORE));
    }

    @Test
    public void player1Win_player2Win_draw_test() {
        assertTrue(stateMachine.sendEvent(MessageBuilder.withPayload(Event.INITIALIZE_TEST)
                .setHeader("player1cards", getPlayer1DrawWinWinCards())
                .setHeader("player2cards", getPlayer2DrawWinWinCards())
                .build()));

        assertEquals(PLAYER_1_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_1_PLAY_CARD_1)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 1))
                .build());

        assertEquals(PLAYER_2_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_2_PLAY_CARD_1)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.CLUBS, 5))
                .build());

        assertEquals(PLAYER_1_TURN_2, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_1_PLAY_CARD_2)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 7))
                .build());

        assertEquals(PLAYER_2_TURN_2, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_2_PLAY_CARD_2)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.CLUBS, 1))
                .build());

        assertEquals(PLAYER_2_TURN_3, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_2_PLAY_CARD_3)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.CLUBS, 4))
                .build());

        assertEquals(PLAYER_1_TURN_3, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_1_PLAY_CARD_3)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 4))
                .build());

        assertEquals(FINAL, stateMachine.getState().getId());
        assertEquals(PLAYER_1, stateMachine.getExtendedState().getVariables().get(WINNER));
        assertEquals(1, stateMachine.getExtendedState().getVariables().get(PLAYER_1_SCORE));
        assertEquals(0, stateMachine.getExtendedState().getVariables().get(PLAYER_2_SCORE));
    }

    @Test
    public void draw_player1Win_test() {
        assertTrue(stateMachine.sendEvent(MessageBuilder.withPayload(Event.INITIALIZE_TEST)
                .setHeader("player1cards", getPlayer1DrawDrawDrawCards())
                .setHeader("player2cards", getPlayer2DrawDrawDrawCards())
                .build()));

        assertEquals(PLAYER_1_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_1_PLAY_CARD_1)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 4))
                .build());

        assertEquals(PLAYER_2_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_2_PLAY_CARD_1)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.GOLDS, 4))
                .build());

        assertEquals(PLAYER_1_TURN_2, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_1_PLAY_CARD_2)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 6))
                .build());

        assertEquals(PLAYER_2_TURN_2, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_2_PLAY_CARD_2)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.GOLDS, 5))
                .build());

        assertEquals(FINAL, stateMachine.getState().getId());
        assertEquals(PLAYER_1, stateMachine.getExtendedState().getVariables().get(WINNER));
        assertEquals(1, stateMachine.getExtendedState().getVariables().get(PLAYER_1_SCORE));
        assertEquals(0, stateMachine.getExtendedState().getVariables().get(PLAYER_2_SCORE));
    }

    @Test
    public void draw_draw_draw_test() {
        assertTrue(stateMachine.sendEvent(MessageBuilder.withPayload(Event.INITIALIZE_TEST)
                .setHeader("player1cards", getPlayer1DrawDrawDrawCards())
                .setHeader("player2cards", getPlayer2DrawDrawDrawCards())
                .build()));

        assertEquals(PLAYER_1_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_1_PLAY_CARD_1)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 4))
                .build());

        assertEquals(PLAYER_2_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_2_PLAY_CARD_1)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.GOLDS, 4))
                .build());

        assertEquals(PLAYER_1_TURN_2, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_1_PLAY_CARD_2)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 5))
                .build());

        assertEquals(PLAYER_2_TURN_2, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_2_PLAY_CARD_2)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.GOLDS, 5))
                .build());

        assertEquals(PLAYER_1_TURN_3, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_1_PLAY_CARD_3)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 6))
                .build());

        assertEquals(PLAYER_2_TURN_3, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_2_PLAY_CARD_3)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.GOLDS, 6))
                .build());

        assertEquals(FINAL, stateMachine.getState().getId());
        assertEquals(PLAYER_1, stateMachine.getExtendedState().getVariables().get(WINNER));
        assertEquals(1, stateMachine.getExtendedState().getVariables().get(PLAYER_1_SCORE));
        assertEquals(0, stateMachine.getExtendedState().getVariables().get(PLAYER_2_SCORE));
    }

    @Test
    public void draw_draw_player1Win_test() {
        assertTrue(stateMachine.sendEvent(MessageBuilder.withPayload(Event.INITIALIZE_TEST)
                .setHeader("player1cards", getPlayer1DrawDrawWinCards())
                .setHeader("player2cards", getPlayer2DrawDrawWinCards())
                .build()));

        assertEquals(PLAYER_1_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_1_PLAY_CARD_1)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 4))
                .build());

        assertEquals(PLAYER_2_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_2_PLAY_CARD_1)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.GOLDS, 4))
                .build());

        assertEquals(PLAYER_1_TURN_2, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_1_PLAY_CARD_2)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 5))
                .build());

        assertEquals(PLAYER_2_TURN_2, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_2_PLAY_CARD_2)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.GOLDS, 5))
                .build());

        assertEquals(PLAYER_1_TURN_3, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_1_PLAY_CARD_3)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 1))
                .build());

        assertEquals(PLAYER_2_TURN_3, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_2_PLAY_CARD_3)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.GOLDS, 1))
                .build());

        assertEquals(FINAL, stateMachine.getState().getId());
        assertEquals(PLAYER_1, stateMachine.getExtendedState().getVariables().get(WINNER));
        assertEquals(1, stateMachine.getExtendedState().getVariables().get(PLAYER_1_SCORE));
        assertEquals(0, stateMachine.getExtendedState().getVariables().get(PLAYER_2_SCORE));
    }

    @Test
    public void player1Win_draw_test() {
        assertTrue(stateMachine.sendEvent(MessageBuilder.withPayload(Event.INITIALIZE_TEST)
                .setHeader("player1cards", getPlayer1DrawDrawDrawCards())
                .setHeader("player2cards", getPlayer2DrawDrawDrawCards())
                .build()));

        assertEquals(PLAYER_1_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_1_PLAY_CARD_1)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 5))
                .build());

        assertEquals(PLAYER_2_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_2_PLAY_CARD_1)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.GOLDS, 4))
                .build());

        assertEquals(PLAYER_1_TURN_2, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_1_PLAY_CARD_2)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 6))
                .build());

        assertEquals(PLAYER_2_TURN_2, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Event.PLAYER_2_PLAY_CARD_2)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.GOLDS, 6))
                .build());

        assertEquals(FINAL, stateMachine.getState().getId());
        assertEquals(PLAYER_1, stateMachine.getExtendedState().getVariables().get(WINNER));
        assertEquals(1, stateMachine.getExtendedState().getVariables().get(PLAYER_1_SCORE));
        assertEquals(0, stateMachine.getExtendedState().getVariables().get(PLAYER_2_SCORE));
    }

    @Test
    public void wrong_event_test() {
        assertTrue(stateMachine.sendEvent(MessageBuilder.withPayload(Event.INITIALIZE_TEST)
                .setHeader("player1cards", getPlayer1Cards())
                .setHeader("player2cards", getPlayer2Cards())
                .build()));
        assertFalse(stateMachine.sendEvent(Event.PLAYER_2_PLAY_CARD_1));
    }

}