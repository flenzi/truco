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

import static f.l.truco.machine.MessageHeaders.PLAYED_CARD;
import static f.l.truco.machine.States.PLAYER_2_TURN;
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
    public void happyPathTest() {
        stateMachine.sendEvent(Events.INITIALIZE_TEST);

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_1_PLAY_CARD)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.SWORDS, 6))
                .build());

        assertEquals(PLAYER_2_TURN, stateMachine.getState().getId());

        stateMachine.sendEvent(MessageBuilder.withPayload(Events.PLAYER_2_PLAY_CARD)
                .setHeader(PLAYED_CARD.toString(), Card.get(Suit.CLUBS, 1))
                .build());

        assertEquals(PLAYER_2_TURN, stateMachine.getState().getId());
    }

    @Test
    public void wrongTurnPlayCardTest() {
        assertTrue(stateMachine.sendEvent(Events.INITIALIZE_TO_PLAYER_1_TURN));
        assertFalse(stateMachine.sendEvent(Events.PLAYER_2_PLAY_CARD));
    }

}