package f.l.truco.config;

import f.l.truco.machine.Events;
import f.l.truco.machine.States;
import f.l.truco.model.Card;
import f.l.truco.model.Suit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.ArrayList;
import java.util.List;

import static f.l.truco.machine.Events.INITIALIZE_TEST;
import static f.l.truco.machine.ExtendedStateVariables.CARDS_PLAYED;
import static f.l.truco.machine.ExtendedStateVariables.PLAYER_1_CARDS;
import static f.l.truco.machine.ExtendedStateVariables.PLAYER_2_CARDS;
import static f.l.truco.machine.ExtendedStateVariables.TURN;
import static f.l.truco.machine.ExtendedStateVariables.TURN_NUMBER;
import static f.l.truco.model.Players.PLAYER_1;

@Configuration
@Import(StateMachineValidationConfiguration.class)
public class StateMachineConfigurationTest extends StateMachineConfiguration {

    @Override
    public void configure(StateMachineTransitionConfigurer<States, Events> transitions)
            throws Exception {
        setTransitions(transitions);

        transitions
                .withExternal()
                .source(States.INITIAL)
                .target(States.PLAYER_1_TURN)
                .event(INITIALIZE_TEST)
                .action(initialSetActionTest());
    }

    @Bean
    public Action<States, Events> initialSetActionTest() {
        return new Action<States, Events>() {
            @Override
            public void execute(StateContext<States, Events> context) {
                List<Card> player1Cards = new ArrayList<>();
                player1Cards.add(Card.get(Suit.SWORDS, 1));
                player1Cards.add(Card.get(Suit.SWORDS, 2));
                player1Cards.add(Card.get(Suit.SWORDS, 6));
                context.getExtendedState().getVariables()
                        .put(PLAYER_1_CARDS, player1Cards);

                List<Card> player2Cards = new ArrayList<>();
                player2Cards.add(Card.get(Suit.GOLDS, 3));
                player2Cards.add(Card.get(Suit.GOLDS, 4));
                player2Cards.add(Card.get(Suit.CLUBS, 1));
                context.getExtendedState().getVariables()
                        .put(PLAYER_2_CARDS, player2Cards);
                context.getExtendedState().getVariables().put(TURN, PLAYER_1);
                context.getExtendedState().getVariables().put(TURN_NUMBER, 1);
                context.getExtendedState().getVariables().put(CARDS_PLAYED, new ArrayList<>(6));
            }

        };
    }

}
