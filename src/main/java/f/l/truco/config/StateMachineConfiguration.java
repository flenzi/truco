package f.l.truco.config;

import static f.l.truco.machine.ExtendedStateVariable.CARDS_PLAYED;
import static f.l.truco.machine.ExtendedStateVariable.DRAW_1;
import static f.l.truco.machine.ExtendedStateVariable.DRAW_2;
import static f.l.truco.machine.ExtendedStateVariable.DRAW_3;
import static f.l.truco.machine.ExtendedStateVariable.GAME_ENDED;
import static f.l.truco.machine.ExtendedStateVariable.HAND;
import static f.l.truco.machine.ExtendedStateVariable.PLAYER_1_CARDS_CURRENT;
import static f.l.truco.machine.ExtendedStateVariable.PLAYER_1_CARDS_ROUND;
import static f.l.truco.machine.ExtendedStateVariable.PLAYER_1_SCORE;
import static f.l.truco.machine.ExtendedStateVariable.PLAYER_2_CARDS_CURRENT;
import static f.l.truco.machine.ExtendedStateVariable.PLAYER_2_CARDS_ROUND;
import static f.l.truco.machine.ExtendedStateVariable.PLAYER_2_SCORE;
import static f.l.truco.machine.ExtendedStateVariable.ROUND_1_WINNER;
import static f.l.truco.machine.ExtendedStateVariable.ROUND_2_WINNER;
import static f.l.truco.machine.ExtendedStateVariable.ROUND_3_WINNER;
import static f.l.truco.machine.ExtendedStateVariable.TURN;
import static f.l.truco.machine.ExtendedStateVariable.TURN_NUMBER;
import static f.l.truco.machine.ExtendedStateVariable.WINNER;
import static f.l.truco.model.Player.PLAYER_1;
import static f.l.truco.model.Player.PLAYER_2;

import f.l.truco.machine.Event;
import f.l.truco.machine.State;
import f.l.truco.model.Card;
import f.l.truco.model.Player;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

@Configuration
@EnableStateMachine
public class StateMachineConfiguration extends EnumStateMachineConfigurerAdapter<State, Event> {

    @Autowired
    private Guard<State, Event> player1PlayCard;

    @Autowired
    private Guard<State, Event> player2PlayCard;

    @Autowired
    private StateMachineListener<State, Event> stateChangeListener;

    @Override
    public void configure(StateMachineConfigurationConfigurer<State, Event> config)
            throws Exception {
        config.withConfiguration().autoStartup(true).listener(stateChangeListener);
    }

    @Override
    public void configure(StateMachineStateConfigurer<State, Event> states) throws Exception {
        states
                .withStates()
                .initial(State.INITIAL)
                .choice(State.COMPUTE_1)
                .choice(State.COMPUTE_2)
                .choice(State.COMPUTE_3)
                .choice(State.COMPUTE_NEXT_PLAYER_TURN_2)
                .choice(State.COMPUTE_NEXT_PLAYER_TURN_3)
                .choice(State.COMPUTE_WINNER)
                .end(State.FINAL)
                .states(EnumSet.allOf(State.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<State, Event> transitions)
            throws Exception {
        setInitialTransitions(transitions);
        setPlayCardTransitionsRound1(transitions);
        setPlayCardTransitionsRound2(transitions);
        setPlayCardTransitionsRound3(transitions);
    }

    private void setInitialTransitions(StateMachineTransitionConfigurer<State, Event> transitions)
            throws Exception {
        transitions
                // Initialize
                .withExternal()
                .source(State.INITIAL)
                .target(State.PLAYER_1_TURN_1)
                .event(Event.INITIALIZE_TO_PLAYER_1_TURN)
                .action(initialSetAction())
                .and()
                .withExternal()
                .source(State.INITIAL)
                .target(State.PLAYER_2_TURN_1)
                .event(Event.INITIALIZE_TO_PLAYER_2_TURN)
                .action(initialSetAction());
    }

    private void setPlayCardTransitionsRound1(
            StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {
        transitions
                .withExternal()
                .source(State.PLAYER_1_TURN_1)
                .target(State.COMPUTE_1)
                .event(Event.PLAYER_1_PLAY_CARD_1)
                .guard(player1PlayCard)

                .and()
                .withExternal()
                .source(State.PLAYER_2_TURN_1)
                .target(State.COMPUTE_1)
                .event(Event.PLAYER_2_PLAY_CARD_1)
                .guard(player2PlayCard)

                // Calculate next turn based on card played.
                // When both player1NextTurn and player2NextTurn are false then there is a round winner
                // and next state(or final) is decided in another choice
                .and()
                .withChoice()
                .source(State.COMPUTE_1)
                .first(State.PLAYER_1_TURN_1, player1NextTurnOdd())
                .then(State.PLAYER_2_TURN_1, player2NextTurnOdd())
                .last(State.COMPUTE_NEXT_PLAYER_TURN_2)

                .and()
                .withChoice()
                .source(State.COMPUTE_NEXT_PLAYER_TURN_2)
                .first(State.PLAYER_1_TURN_2, player1NextTurn2())
                .then(State.PLAYER_2_TURN_2, player2NextTurn2())
                .last(State.NONE);
    }

    private void setPlayCardTransitionsRound2(
            StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {
        transitions
                .withExternal()
                .source(State.PLAYER_1_TURN_2)
                .target(State.COMPUTE_2)
                .event(Event.PLAYER_1_PLAY_CARD_2)
                .guard(player1PlayCard)

                .and()
                .withExternal()
                .source(State.PLAYER_2_TURN_2)
                .target(State.COMPUTE_2)
                .event(Event.PLAYER_2_PLAY_CARD_2)
                .guard(player2PlayCard)

                .and()
                .withChoice()
                .source(State.COMPUTE_2)
                .first(State.PLAYER_1_TURN_2, player1NextTurnOdd())
                .then(State.PLAYER_2_TURN_2, player2NextTurnOdd())
                .last(State.COMPUTE_NEXT_PLAYER_TURN_3)

                .and()
                .withChoice()
                .source(State.COMPUTE_NEXT_PLAYER_TURN_3)
                .first(State.PLAYER_1_TURN_3, player1NextTurn3())
                .then(State.PLAYER_2_TURN_3, player2NextTurn3())
                .last(State.FINAL);
    }

    private void setPlayCardTransitionsRound3(
            StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {
        transitions
                .withExternal()
                .source(State.PLAYER_1_TURN_3)
                .target(State.COMPUTE_3)
                .event(Event.PLAYER_1_PLAY_CARD_3)
                .guard(player1PlayCard)

                .and()
                .withExternal()
                .source(State.PLAYER_2_TURN_3)
                .target(State.COMPUTE_3)
                .event(Event.PLAYER_2_PLAY_CARD_3)
                .guard(player2PlayCard)

                .and()
                .withChoice()
                .source(State.COMPUTE_3)
                .first(State.PLAYER_1_TURN_3, player1NextTurnOdd())
                .then(State.PLAYER_2_TURN_3, player2NextTurnOdd())
                .last(State.COMPUTE_WINNER)

                .and()
                .withChoice()
                .source(State.COMPUTE_WINNER)
                .first(State.FINAL, player1WinTurn3())
                .then(State.FINAL, player2WinTurn3())
                .last(State.NONE);
    }

    /**
     * Create players and distribute cards
     */
    @Bean
    public Action<State, Event> initialSetAction() {
        return new Action<State, Event>() {
            @Override
            public void execute(StateContext<State, Event> context) {
                List<Card> cards = getRoundCards();
                context.getExtendedState().getVariables().put(PLAYER_1_CARDS_CURRENT, new ArrayList<>(cards.subList(0, 3)));
                context.getExtendedState().getVariables().put(PLAYER_1_CARDS_ROUND, new ArrayList<>(cards.subList(0, 3)));
                context.getExtendedState().getVariables().put(PLAYER_2_CARDS_CURRENT, new ArrayList<>(cards.subList(3, 6)));
                context.getExtendedState().getVariables().put(PLAYER_2_CARDS_ROUND, new ArrayList<>(cards.subList(3, 6)));

                if (context.getEvent() == Event.INITIALIZE_TO_PLAYER_1_TURN) {
                    context.getExtendedState().getVariables().put(TURN, PLAYER_1);
                    context.getExtendedState().getVariables().put(HAND, PLAYER_1);
                } else if (context.getEvent() == Event.INITIALIZE_TO_PLAYER_2_TURN) {
                    context.getExtendedState().getVariables().put(TURN, PLAYER_2);
                    context.getExtendedState().getVariables().put(HAND, PLAYER_2);
                }

                context.getExtendedState().getVariables().put(PLAYER_1_SCORE, 0);
                context.getExtendedState().getVariables().put(PLAYER_2_SCORE, 0);
                context.getExtendedState().getVariables().put(TURN_NUMBER, 1);
                context.getExtendedState().getVariables().put(CARDS_PLAYED, new ArrayList<>(6));
            }

            private List<Card> getRoundCards() {
                Set<Card> cards = new HashSet<>(6);
                while (cards.size() < 6) {
                    cards.add(Card.generateRandomCard());
                }
                return new ArrayList<>(cards);
            }
        };
    }

    @Bean
    public Guard<State, Event> player1NextTurnOdd() {
        return playerNextTurnOdd(PLAYER_1);
    }

    @Bean
    public Guard<State, Event> player2NextTurnOdd() {
        return playerNextTurnOdd(PLAYER_2);
    }

    private Guard<State, Event> playerNextTurnOdd(Player player) {
        return new Guard<State, Event>() {
            @Override
            public boolean evaluate(StateContext<State, Event> context) {
                int turnNumber = (int) context.getExtendedState().getVariables().get(TURN_NUMBER);
                if (player != context.getExtendedState().getVariables().get(TURN) && turnNumber % 2 == 1) {
                    context.getExtendedState().getVariables().put(TURN, player);
                    context.getExtendedState().getVariables().put(TURN_NUMBER, ++turnNumber);
                    return true;
                }
                return false;
            }
        };
    }

    // Finish round 1, next player calculation
    @Bean
    public Guard<State, Event> player1NextTurn2() {
        return playerNextTurn2(PLAYER_1);
    }

    @Bean
    public Guard<State, Event> player2NextTurn2() {
        return playerNextTurn2(PLAYER_2);
    }

    private Guard<State, Event> playerNextTurn2(Player player) {
        return context -> {
            List<Card> cards = (ArrayList<Card>) context.getExtendedState().getVariables().get(CARDS_PLAYED);
            int turnNumber = (int) context.getExtendedState().getVariables().get(TURN_NUMBER);
            int resultStage = cards.get(cards.size() - 2).compareTo(cards.get(cards.size() - 1));

            if (resultStage == 0 && player == context.getExtendedState().getVariables().get(HAND)) {
                context.getExtendedState().getVariables().put(DRAW_1, true);
                context.getExtendedState().getVariables().put(TURN, player);
                context.getExtendedState().getVariables().put(TURN_NUMBER, ++turnNumber);
                return true;
            } else if ((resultStage == 1 && player != context.getExtendedState().getVariables().get(TURN))
                    || resultStage == -1 && player == context.getExtendedState().getVariables().get(TURN)) {
                context.getExtendedState().getVariables().put(TURN, player);
                context.getExtendedState().getVariables().put(ROUND_1_WINNER, player);
                context.getExtendedState().getVariables().put(TURN_NUMBER, ++turnNumber);
                return true;
            } else {
                return false;
            }
        };
    }

    // Finish round 2, next player calculation
    @Bean
    public Guard<State, Event> player1NextTurn3() {
        return playerNextTurn3(PLAYER_1);
    }

    @Bean
    public Guard<State, Event> player2NextTurn3() {
        return playerNextTurn3(PLAYER_2);
    }

    private Guard<State, Event> playerNextTurn3(Player player) {
        return context -> {
            if (!(boolean) context.getExtendedState().getVariables().getOrDefault(GAME_ENDED, false)) {
                List<Card> cards = (ArrayList<Card>) context.getExtendedState().getVariables().get(CARDS_PLAYED);
                int turnNumber = (int) context.getExtendedState().getVariables().get(TURN_NUMBER);
                int resultStage = cards.get(cards.size() - 2).compareTo(cards.get(cards.size() - 1));

                if (resultStage == 0) {
                    context.getExtendedState().getVariables().put(DRAW_2, true);
                    if ((boolean) context.getExtendedState().getVariables().getOrDefault(DRAW_1, false)
                            && player == context.getExtendedState().getVariables().get(HAND)) {
                        context.getExtendedState().getVariables().put(TURN, player);
                        context.getExtendedState().getVariables().put(TURN_NUMBER, ++turnNumber);
                        return true;
                    } else if (!(boolean) context.getExtendedState().getVariables().getOrDefault(DRAW_1, false)) {
                        // Winner found
                        context.getExtendedState().getVariables().put(GAME_ENDED, true);
                        context.getExtendedState().getVariables()
                                .put(WINNER, context.getExtendedState().getVariables().get(ROUND_1_WINNER));
                        calculateScores(context);
                        return false;
                    }
                } else if ((resultStage == 1 && player != context.getExtendedState().getVariables().get(TURN))
                        || resultStage == -1 && player == context.getExtendedState().getVariables().get(TURN)) {

                    context.getExtendedState().getVariables().put(ROUND_2_WINNER, player);

                    if ((boolean) context.getExtendedState().getVariables().getOrDefault(DRAW_1, false)) {
                        // Winner found
                        context.getExtendedState().getVariables().put(GAME_ENDED, true);
                        context.getExtendedState().getVariables().put(WINNER, player);
                        calculateScores(context);
                        return false;
                    }

                    if (context.getExtendedState().getVariables().get(ROUND_1_WINNER) == context.getExtendedState().getVariables()
                            .get(ROUND_2_WINNER)) {
                        // Winner found
                        context.getExtendedState().getVariables().put(GAME_ENDED, true);
                        context.getExtendedState().getVariables().put(WINNER, player);
                        calculateScores(context);
                        return false;
                    } else {
                        context.getExtendedState().getVariables().put(TURN, player);
                        context.getExtendedState().getVariables().put(ROUND_2_WINNER, player);
                        context.getExtendedState().getVariables().put(TURN_NUMBER, ++turnNumber);
                        return true;
                    }
                }
            }
            return false;
        };
    }

    // Finish round 3, winner calculation
    @Bean
    public Guard<State, Event> player1WinTurn3() {
        return playerWinTurn3(PLAYER_1);
    }

    @Bean
    public Guard<State, Event> player2WinTurn3() {
        return playerWinTurn3(PLAYER_2);
    }

    private Guard<State, Event> playerWinTurn3(Player player) {
        return new Guard<State, Event>() {
            @Override
            public boolean evaluate(StateContext<State, Event> context) {
                if (!(boolean) context.getExtendedState().getVariables().getOrDefault(GAME_ENDED, false)) {
                    List<Card> cards = (ArrayList<Card>) context.getExtendedState().getVariables().get(CARDS_PLAYED);
                    int resultStage = cards.get(cards.size() - 2).compareTo(cards.get(cards.size() - 1));

                    if (resultStage == 0) {
                        if ((boolean) context.getExtendedState().getVariables().getOrDefault(DRAW_1, false) && (boolean) context
                                .getExtendedState().getVariables().getOrDefault(DRAW_2, false)) {
                            context.getExtendedState().getVariables().put(WINNER, context.getExtendedState().getVariables().get(HAND));
                        } else {
                            context.getExtendedState().getVariables()
                                    .put(WINNER, context.getExtendedState().getVariables().get(ROUND_1_WINNER));
                        }
                        context.getExtendedState().getVariables().put(DRAW_3, true);
                        context.getExtendedState().getVariables().put(GAME_ENDED, true);
                        calculateScores(context);
                        return true;
                    } else if ((resultStage == 1 && player != context.getExtendedState().getVariables().get(TURN))
                            || resultStage == -1 && player == context.getExtendedState().getVariables().get(TURN)) {
                        context.getExtendedState().getVariables().put(ROUND_3_WINNER, player);
                        context.getExtendedState().getVariables().put(WINNER, player);
                        context.getExtendedState().getVariables().put(GAME_ENDED, true);
                        calculateScores(context);
                        return true;
                    }
                }
                return false;
            }
        };
    }

    private void calculateScores(StateContext<State, Event> context) {
        Player player = (Player) context.getExtendedState().getVariables().get(WINNER);
        if (player == PLAYER_1) {
            int currentScore = (int) context.getExtendedState().getVariables().getOrDefault(PLAYER_1_SCORE, 0);
            context.getExtendedState().getVariables().put(PLAYER_1_SCORE, ++currentScore);
        } else {
            int currentScore = (int) context.getExtendedState().getVariables().getOrDefault(PLAYER_2_SCORE, 0);
            context.getExtendedState().getVariables().put(PLAYER_2_SCORE, ++currentScore);
        }
    }
}
