package f.l.truco.config;

import f.l.truco.machine.Events;
import f.l.truco.machine.States;
import f.l.truco.model.Card;
import f.l.truco.model.Players;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static f.l.truco.machine.ExtendedStateVariables.CARDS_PLAYED;
import static f.l.truco.machine.ExtendedStateVariables.HAND;
import static f.l.truco.machine.ExtendedStateVariables.PLAYER_1_CARDS;
import static f.l.truco.machine.ExtendedStateVariables.PLAYER_2_CARDS;
import static f.l.truco.machine.ExtendedStateVariables.TURN;
import static f.l.truco.machine.ExtendedStateVariables.TURN_NUMBER;
import static f.l.truco.model.Players.PLAYER_1;
import static f.l.truco.model.Players.PLAYER_2;

@Configuration
@EnableStateMachine
public class StateMachineConfiguration extends EnumStateMachineConfigurerAdapter<States, Events> {

    @Autowired
    public Guard<States, Events> player1PlayCardValidate;

    @Autowired
    public Guard<States, Events> player2PlayCardValidate;

    @Override
    public void configure(StateMachineConfigurationConfigurer<States, Events> config)
            throws Exception {
        config.withConfiguration()
                .autoStartup(true)
                .listener(listener());
    }

    @Override
    public void configure(StateMachineStateConfigurer<States, Events> states) throws Exception {
        states.withStates()
                .initial(States.INITIAL)
                .choice(States.COMPUTE)
                .end(States.FINAL)
                .states(EnumSet.allOf(States.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<States, Events> transitions)
            throws Exception {
        setTransitions(transitions);
    }

    protected void setTransitions(StateMachineTransitionConfigurer<States, Events> transitions) throws Exception {
        transitions
                // Initialize
                .withExternal()
                .source(States.INITIAL)
                .target(States.PLAYER_1_TURN)
                .event(Events.INITIALIZE_TO_PLAYER_1_TURN)
                .action(initialSetAction())

                .and()
                .withExternal()
                .source(States.INITIAL)
                .target(States.PLAYER_2_TURN)
                .event(Events.INITIALIZE_TO_PLAYER_2_TURN)
                .action(initialSetAction())

                // Play card
                .and()
                .withExternal()
                .source(States.PLAYER_1_TURN)
                .target(States.COMPUTE)
                .event(Events.PLAYER_1_PLAY_CARD)
                .guard(player1PlayCardValidate)

                .and()
                .withExternal()
                .source(States.PLAYER_2_TURN)
                .target(States.COMPUTE)
                .event(Events.PLAYER_2_PLAY_CARD)
                .guard(player2PlayCardValidate)

                // Calculate next turn based on card played
                .and()
                .withChoice()
                .source(States.COMPUTE)
                .first(States.PLAYER_1_TURN, player1NextTurn())
                .then(States.PLAYER_2_TURN, player2NextTurn())
                .last(States.FINAL);
    }

    @Bean
    public StateMachineListener<States, Events> listener() {
        return new StateMachineListenerAdapter<States, Events>() {
            @Override
            public void stateChanged(State<States, Events> from, State<States, Events> to) {
                System.out.println("State change to " + to.getId());
            }
        };
    }

    /**
     * Creates the players and distributes the cards
     */
    @Bean
    public Action<States, Events> initialSetAction() {
        return new Action<States, Events>() {
            @Override
            public void execute(StateContext<States, Events> context) {
                List<Card> cards = getCards();
                context.getExtendedState().getVariables().put(PLAYER_1_CARDS, new ArrayList<>(cards.subList(0, 3)));
                context.getExtendedState().getVariables().put(PLAYER_2_CARDS, new ArrayList<>(cards.subList(3, 6)));

                if (context.getEvent() == Events.INITIALIZE_TO_PLAYER_1_TURN) {
                    context.getExtendedState().getVariables().put(TURN, PLAYER_1);
                    context.getExtendedState().getVariables().put(HAND, PLAYER_1);
                } else if (context.getEvent() == Events.INITIALIZE_TO_PLAYER_2_TURN) {
                    context.getExtendedState().getVariables().put(TURN, PLAYER_2);
                    context.getExtendedState().getVariables().put(HAND, PLAYER_2);
                }
                context.getExtendedState().getVariables().put(TURN_NUMBER, 1);
                context.getExtendedState().getVariables().put(CARDS_PLAYED, new ArrayList<>(6));
            }

            private List<Card> getCards() {
                Set<Card> cards = new HashSet<>(6);
                while (cards.size() < 6) {
                    cards.add(Card.generateRandomCard());
                }
                return new ArrayList<>(cards);
            }
        };
    }

    @Bean
    public Guard<States, Events> player1NextTurn() {
        return playerNextTurn(PLAYER_1, PLAYER_2);
    }

    @Bean
    public Guard<States, Events> player2NextTurn() {
        return playerNextTurn(PLAYER_2, PLAYER_1);
    }

    private Guard<States, Events> playerNextTurn(Players hand, Players foot) {
        return new Guard<States, Events>() {

            @Override
            public boolean evaluate(StateContext<States, Events> context) {
                int turnNumber = (int) context.getExtendedState().getVariables().get(TURN_NUMBER);

                if (foot == context.getExtendedState().getVariables().get(TURN) &&
                        turnNumber % 2 == 1) {
                    context.getExtendedState().getVariables().put(TURN, hand);
                    context.getExtendedState().getVariables().put(TURN_NUMBER, ++turnNumber);
                    return true;
                } else if (turnNumber % 2 == 0) {
                    List<Card> cards = (ArrayList<Card>) context.getExtendedState().getVariables().get(CARDS_PLAYED);
                    int resultStage = cards.get(cards.size() - 2).compareTo(cards.get(cards.size() - 1));
                    if (foot == context.getExtendedState().getVariables().get(TURN)) {
                        if (resultStage == 1) {
                            return true;
                        } else if (resultStage == -1) {
                            return false;
                        }
                    } else if (hand == context.getExtendedState().getVariables().get(TURN)) {
                        if (resultStage == -1) {
                            return true;
                        } else if (resultStage == 1) {
                            return false;
                        }
                    }
                }
                return false;
            }
        };
    }
}
