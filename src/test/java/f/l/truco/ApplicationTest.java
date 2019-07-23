package f.l.truco;

import f.l.truco.config.StateMachineConfigurationTest;
import f.l.truco.machine.Events;
import f.l.truco.machine.States;
import f.l.truco.model.Card;
import f.l.truco.model.Suit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static f.l.truco.TestUtils.getPlayer1Cards;
import static f.l.truco.TestUtils.getPlayer1DrawDrawDrawCards;
import static f.l.truco.TestUtils.getPlayer1DrawDrawWinCards;
import static f.l.truco.TestUtils.getPlayer1DrawWinWinCards;
import static f.l.truco.TestUtils.getPlayer2Cards;
import static f.l.truco.TestUtils.getPlayer2DrawDrawDrawCards;
import static f.l.truco.TestUtils.getPlayer2DrawDrawWinCards;
import static f.l.truco.TestUtils.getPlayer2DrawWinWinCards;
import static f.l.truco.machine.ExtendedStateVariables.WINNER;
import static f.l.truco.machine.MessageHeaders.PLAYED_CARD;
import static f.l.truco.machine.States.FINAL;
import static f.l.truco.machine.States.PLAYER_1_TURN_1;
import static f.l.truco.machine.States.PLAYER_1_TURN_2;
import static f.l.truco.machine.States.PLAYER_1_TURN_3;
import static f.l.truco.machine.States.PLAYER_2_TURN_1;
import static f.l.truco.machine.States.PLAYER_2_TURN_2;
import static f.l.truco.machine.States.PLAYER_2_TURN_3;
import static f.l.truco.model.Players.PLAYER_1;
import static f.l.truco.model.Players.PLAYER_2;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = StateMachineConfigurationTest.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ApplicationTest {

    @Autowired
    private StateMachine<States, Events> stateMachine;

    @Test
    public void player1Win_player1Win_test() {
        assertTrue(stateMachine.sendEvent(MessageBuilder.withPayload(Events.INITIALIZE_TEST)
                .setHeader("player1cards", getPlayer1Cards())
                .setHeader("player2cards", getPlayer2Cards())
                .build()));

        assertEquals(PLAYER_1_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_1_PLAY_CARD_1)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 1))
                .build());

        assertEquals(PLAYER_2_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_2_PLAY_CARD_1)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.CLUBS, 1))
                .build());

        assertEquals(PLAYER_1_TURN_2, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_1_PLAY_CARD_2)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 2))
                .build());

        assertEquals(PLAYER_2_TURN_2, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_2_PLAY_CARD_2)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.GOLDS, 4))
                .build());

        assertEquals(FINAL, stateMachine.getState().getId());
        assertEquals(PLAYER_1, stateMachine.getExtendedState().getVariables().get(WINNER));
    }

    @Test
    public void player2Win_player2Win_test() {
        assertTrue(stateMachine.sendEvent(MessageBuilder.withPayload(Events.INITIALIZE_TEST)
                .setHeader("player1cards", getPlayer1Cards())
                .setHeader("player2cards", getPlayer2Cards())
                .build()));

        assertEquals(PLAYER_1_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_1_PLAY_CARD_1)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 2))
                .build());

        assertEquals(PLAYER_2_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_2_PLAY_CARD_1)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.CLUBS, 1))
                .build());

        assertEquals(PLAYER_2_TURN_2, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_2_PLAY_CARD_2)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.GOLDS, 3))
                .build());

        assertEquals(PLAYER_1_TURN_2, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_1_PLAY_CARD_2)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 6))
                .build());

        assertEquals(FINAL, stateMachine.getState().getId());
        assertEquals(PLAYER_2, stateMachine.getExtendedState().getVariables().get(WINNER));
    }

    @Test
    public void player2Win_player1Win_player_1Win_test() {
        assertTrue(stateMachine.sendEvent(MessageBuilder.withPayload(Events.INITIALIZE_TEST)
                .setHeader("player1cards", getPlayer1Cards())
                .setHeader("player2cards", getPlayer2Cards())
                .build()));

        assertEquals(PLAYER_1_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_1_PLAY_CARD_1)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 6))
                .build());

        assertEquals(PLAYER_2_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_2_PLAY_CARD_1)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.CLUBS, 1))
                .build());

        assertEquals(PLAYER_2_TURN_2, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_2_PLAY_CARD_2)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.GOLDS, 3))
                .build());

        assertEquals(PLAYER_1_TURN_2, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_1_PLAY_CARD_2)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 1))
                .build());

        assertEquals(PLAYER_1_TURN_3, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_1_PLAY_CARD_3)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 2))
                .build());

        assertEquals(PLAYER_2_TURN_3, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_2_PLAY_CARD_3)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.GOLDS, 4))
                .build());

        assertEquals(FINAL, stateMachine.getState().getId());
        assertEquals(PLAYER_1, stateMachine.getExtendedState().getVariables().get(WINNER));
    }

    @Test
    public void player1Win_player2Win_player_1Win_test() {
        assertTrue(stateMachine.sendEvent(MessageBuilder.withPayload(Events.INITIALIZE_TEST)
                .setHeader("player1cards", getPlayer1Cards())
                .setHeader("player2cards", getPlayer2Cards())
                .build()));

        assertEquals(PLAYER_1_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_1_PLAY_CARD_1)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 1))
                .build());

        assertEquals(PLAYER_2_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_2_PLAY_CARD_1)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.CLUBS, 1))
                .build());

        assertEquals(PLAYER_1_TURN_2, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_1_PLAY_CARD_2)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 6))
                .build());

        assertEquals(PLAYER_2_TURN_2, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_2_PLAY_CARD_2)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.GOLDS, 3))
                .build());

        assertEquals(PLAYER_2_TURN_3, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_2_PLAY_CARD_3)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.GOLDS, 4))
                .build());

        assertEquals(PLAYER_1_TURN_3, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_1_PLAY_CARD_3)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 2))
                .build());

        assertEquals(FINAL, stateMachine.getState().getId());
        assertEquals(PLAYER_1, stateMachine.getExtendedState().getVariables().get(WINNER));
    }

    @Test
    public void player1Win_player2Win_draw_test() {
        assertTrue(stateMachine.sendEvent(MessageBuilder.withPayload(Events.INITIALIZE_TEST)
                .setHeader("player1cards", getPlayer1DrawWinWinCards())
                .setHeader("player2cards", getPlayer2DrawWinWinCards())
                .build()));

        assertEquals(PLAYER_1_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_1_PLAY_CARD_1)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 1))
                .build());

        assertEquals(PLAYER_2_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_2_PLAY_CARD_1)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.CLUBS, 5))
                .build());

        assertEquals(PLAYER_1_TURN_2, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_1_PLAY_CARD_2)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 7))
                .build());

        assertEquals(PLAYER_2_TURN_2, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_2_PLAY_CARD_2)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.CLUBS, 1))
                .build());

        assertEquals(PLAYER_2_TURN_3, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_2_PLAY_CARD_3)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.CLUBS, 4))
                .build());

        assertEquals(PLAYER_1_TURN_3, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_1_PLAY_CARD_3)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 4))
                .build());

        assertEquals(FINAL, stateMachine.getState().getId());
        assertEquals(PLAYER_1, stateMachine.getExtendedState().getVariables().get(WINNER));
    }

    @Test
    public void draw_player1Win_test() {
        assertTrue(stateMachine.sendEvent(MessageBuilder.withPayload(Events.INITIALIZE_TEST)
                .setHeader("player1cards", getPlayer1DrawDrawDrawCards())
                .setHeader("player2cards", getPlayer2DrawDrawDrawCards())
                .build()));

        assertEquals(PLAYER_1_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_1_PLAY_CARD_1)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 4))
                .build());

        assertEquals(PLAYER_2_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_2_PLAY_CARD_1)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.GOLDS, 4))
                .build());

        assertEquals(PLAYER_1_TURN_2, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_1_PLAY_CARD_2)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 6))
                .build());

        assertEquals(PLAYER_2_TURN_2, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_2_PLAY_CARD_2)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.GOLDS, 5))
                .build());

        assertEquals(FINAL, stateMachine.getState().getId());
        assertEquals(PLAYER_1, stateMachine.getExtendedState().getVariables().get(WINNER));
    }

    @Test
    public void draw_draw_draw_test() {
        assertTrue(stateMachine.sendEvent(MessageBuilder.withPayload(Events.INITIALIZE_TEST)
                .setHeader("player1cards", getPlayer1DrawDrawDrawCards())
                .setHeader("player2cards", getPlayer2DrawDrawDrawCards())
                .build()));

        assertEquals(PLAYER_1_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_1_PLAY_CARD_1)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 4))
                .build());

        assertEquals(PLAYER_2_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_2_PLAY_CARD_1)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.GOLDS, 4))
                .build());

        assertEquals(PLAYER_1_TURN_2, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_1_PLAY_CARD_2)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 5))
                .build());

        assertEquals(PLAYER_2_TURN_2, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_2_PLAY_CARD_2)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.GOLDS, 5))
                .build());

        assertEquals(PLAYER_1_TURN_3, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_1_PLAY_CARD_3)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 6))
                .build());

        assertEquals(PLAYER_2_TURN_3, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_2_PLAY_CARD_3)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.GOLDS, 6))
                .build());

        assertEquals(FINAL, stateMachine.getState().getId());
        assertEquals(PLAYER_1, stateMachine.getExtendedState().getVariables().get(WINNER));
    }

    @Test
    public void draw_draw_player1Win_test() {
        assertTrue(stateMachine.sendEvent(MessageBuilder.withPayload(Events.INITIALIZE_TEST)
                .setHeader("player1cards", getPlayer1DrawDrawWinCards())
                .setHeader("player2cards", getPlayer2DrawDrawWinCards())
                .build()));

        assertEquals(PLAYER_1_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_1_PLAY_CARD_1)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 4))
                .build());

        assertEquals(PLAYER_2_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_2_PLAY_CARD_1)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.GOLDS, 4))
                .build());

        assertEquals(PLAYER_1_TURN_2, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_1_PLAY_CARD_2)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 5))
                .build());

        assertEquals(PLAYER_2_TURN_2, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_2_PLAY_CARD_2)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.GOLDS, 5))
                .build());

        assertEquals(PLAYER_1_TURN_3, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_1_PLAY_CARD_3)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 1))
                .build());

        assertEquals(PLAYER_2_TURN_3, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_2_PLAY_CARD_3)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.GOLDS, 1))
                .build());

        assertEquals(FINAL, stateMachine.getState().getId());
        assertEquals(PLAYER_1, stateMachine.getExtendedState().getVariables().get(WINNER));
    }

    @Test
    public void player1Win_draw_test() {
        assertTrue(stateMachine.sendEvent(MessageBuilder.withPayload(Events.INITIALIZE_TEST)
                .setHeader("player1cards", getPlayer1DrawDrawDrawCards())
                .setHeader("player2cards", getPlayer2DrawDrawDrawCards())
                .build()));

        assertEquals(PLAYER_1_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_1_PLAY_CARD_1)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 5))
                .build());

        assertEquals(PLAYER_2_TURN_1, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_2_PLAY_CARD_1)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.GOLDS, 4))
                .build());

        assertEquals(PLAYER_1_TURN_2, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_1_PLAY_CARD_2)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 6))
                .build());

        assertEquals(PLAYER_2_TURN_2, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_2_PLAY_CARD_2)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.GOLDS, 6))
                .build());

        assertEquals(FINAL, stateMachine.getState().getId());
        assertEquals(PLAYER_1, stateMachine.getExtendedState().getVariables().get(WINNER));
    }

    @Test
    public void wrong_event_test() {
        assertTrue(stateMachine.sendEvent(MessageBuilder.withPayload(Events.INITIALIZE_TEST)
                .setHeader("player1cards", getPlayer1Cards())
                .setHeader("player2cards", getPlayer2Cards())
                .build()));
        assertFalse(stateMachine.sendEvent(Events.PLAYER_2_PLAY_CARD_1));
    }

}