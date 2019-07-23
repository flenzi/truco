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

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static f.l.truco.machine.ExtendedStateVariables.CARDS_PLAYED;
import static f.l.truco.machine.ExtendedStateVariables.DRAW_1;
import static f.l.truco.machine.ExtendedStateVariables.DRAW_2;
import static f.l.truco.machine.ExtendedStateVariables.DRAW_3;
import static f.l.truco.machine.ExtendedStateVariables.GAME_ENDED;
import static f.l.truco.machine.ExtendedStateVariables.HAND;
import static f.l.truco.machine.ExtendedStateVariables.PLAYER_1_CARDS;
import static f.l.truco.machine.ExtendedStateVariables.PLAYER_2_CARDS;
import static f.l.truco.machine.ExtendedStateVariables.ROUND_1_WINNER;
import static f.l.truco.machine.ExtendedStateVariables.ROUND_2_WINNER;
import static f.l.truco.machine.ExtendedStateVariables.ROUND_3_WINNER;
import static f.l.truco.machine.ExtendedStateVariables.TURN;
import static f.l.truco.machine.ExtendedStateVariables.TURN_NUMBER;
import static f.l.truco.machine.ExtendedStateVariables.WINNER;
import static f.l.truco.model.Players.PLAYER_1;
import static f.l.truco.model.Players.PLAYER_2;

@Configuration
@EnableStateMachine
public class StateMachineConfiguration extends EnumStateMachineConfigurerAdapter<States, Events> {

    @Autowired
    private Guard<States, Events> player1PlayCard;

    @Autowired
    private Guard<States, Events> player2PlayCard;

    @Autowired
    private StateMachineListener<States, Events> stateChangeListener;

    @Override
    public void configure(StateMachineConfigurationConfigurer<States, Events> config)
            throws Exception {
        config.withConfiguration()
                .autoStartup(true)
                .listener(stateChangeListener);
    }

    @Override
    public void configure(StateMachineStateConfigurer<States, Events> states) throws Exception {
        states.withStates()
                .initial(States.INITIAL)
                .choice(States.COMPUTE_1)
                .choice(States.COMPUTE_2)
                .choice(States.COMPUTE_3)
                .choice(States.COMPUTE_NEXT_PLAYER_TURN_2)
                .choice(States.COMPUTE_NEXT_PLAYER_TURN_3)
                .choice(States.COMPUTE_WINNER)
                .end(States.FINAL)
                .states(EnumSet.allOf(States.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<States, Events> transitions)
            throws Exception {
        setInitialTransitions(transitions);
        setPlayCardTransitionsRound1(transitions);
        setPlayCardTransitionsRound2(transitions);
        setPlayCardTransitionsRound3(transitions);
    }

    private void setInitialTransitions(StateMachineTransitionConfigurer<States, Events> transitions) throws Exception {
        transitions
                // Initialize
                .withExternal()
                .source(States.INITIAL)
                .target(States.PLAYER_1_TURN_1)
                .event(Events.INITIALIZE_TO_PLAYER_1_TURN)
                .action(initialSetAction())

                .and()
                .withExternal()
                .source(States.INITIAL)
                .target(States.PLAYER_2_TURN_1)
                .event(Events.INITIALIZE_TO_PLAYER_2_TURN)
                .action(initialSetAction());
    }

    private void setPlayCardTransitionsRound1(StateMachineTransitionConfigurer<States, Events> transitions) throws Exception {
        transitions
                .withExternal()
                .source(States.PLAYER_1_TURN_1)
                .target(States.COMPUTE_1)
                .event(Events.PLAYER_1_PLAY_CARD_1)
                .guard(player1PlayCard)

                .and()
                .withExternal()
                .source(States.PLAYER_2_TURN_1)
                .target(States.COMPUTE_1)
                .event(Events.PLAYER_2_PLAY_CARD_1)
                .guard(player2PlayCard)

                // Calculate next turn based on card played.
                // When both player1NextTurn and player2NextTurn are false then there is a round winner
                // and next state(or final) is decided in another choice
                .and()
                .withChoice()
                .source(States.COMPUTE_1)
                .first(States.PLAYER_1_TURN_1, player1NextTurnOdd())
                .then(States.PLAYER_2_TURN_1, player2NextTurnOdd())
                .last(States.COMPUTE_NEXT_PLAYER_TURN_2)

                .and()
                .withChoice()
                .source(States.COMPUTE_NEXT_PLAYER_TURN_2)
                .first(States.PLAYER_1_TURN_2, player1NextTurn2())
                .then(States.PLAYER_2_TURN_2, player2NextTurn2())
                .last(States.NONE);
    }

    private void setPlayCardTransitionsRound2(StateMachineTransitionConfigurer<States, Events> transitions) throws Exception {
        transitions
                .withExternal()
                .source(States.PLAYER_1_TURN_2)
                .target(States.COMPUTE_2)
                .event(Events.PLAYER_1_PLAY_CARD_2)
                .guard(player1PlayCard)

                .and()
                .withExternal()
                .source(States.PLAYER_2_TURN_2)
                .target(States.COMPUTE_2)
                .event(Events.PLAYER_2_PLAY_CARD_2)
                .guard(player2PlayCard)

                .and()
                .withChoice()
                .source(States.COMPUTE_2)
                .first(States.PLAYER_1_TURN_2, player1NextTurnOdd())
                .then(States.PLAYER_2_TURN_2, player2NextTurnOdd())
                .last(States.COMPUTE_NEXT_PLAYER_TURN_3)

                .and()
                .withChoice()
                .source(States.COMPUTE_NEXT_PLAYER_TURN_3)
                .first(States.PLAYER_1_TURN_3, player1NextTurn3())
                .then(States.PLAYER_2_TURN_3, player2NextTurn3())
                .last(States.FINAL);
    }

    private void setPlayCardTransitionsRound3(StateMachineTransitionConfigurer<States, Events> transitions) throws Exception {
        transitions
                .withExternal()
                .source(States.PLAYER_1_TURN_3)
                .target(States.COMPUTE_3)
                .event(Events.PLAYER_1_PLAY_CARD_3)
                .guard(player1PlayCard)

                .and()
                .withExternal()
                .source(States.PLAYER_2_TURN_3)
                .target(States.COMPUTE_3)
                .event(Events.PLAYER_2_PLAY_CARD_3)
                .guard(player2PlayCard)

                .and()
                .withChoice()
                .source(States.COMPUTE_3)
                .first(States.PLAYER_1_TURN_3, player1NextTurnOdd())
                .then(States.PLAYER_2_TURN_3, player2NextTurnOdd())
                .last(States.COMPUTE_WINNER)

                .and()
                .withChoice()
                .source(States.COMPUTE_WINNER)
                .first(States.FINAL, player1WinTurn3())
                .then(States.FINAL, player2WinTurn3())
                .last(States.FINAL);
    }

    /**
     * Create players and distribute cards
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
    public Guard<States, Events> player1NextTurnOdd() {
        return playerNextTurnOdd(PLAYER_1);
    }

    @Bean
    public Guard<States, Events> player2NextTurnOdd() {
        return playerNextTurnOdd(PLAYER_2);
    }

    private Guard<States, Events> playerNextTurnOdd(Players player) {
        return new Guard<States, Events>() {
            @Override
            public boolean evaluate(StateContext<States, Events> context) {
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

    //Finish round 1, next player calculation
    @Bean
    public Guard<States, Events> player1NextTurn2() {
        return playerNextTurn2(PLAYER_1);
    }

    @Bean
    public Guard<States, Events> player2NextTurn2() {
        return playerNextTurn2(PLAYER_2);
    }

    private Guard<States, Events> playerNextTurn2(Players player) {
        return context -> {
            List<Card> cards = (ArrayList<Card>) context.getExtendedState().getVariables().get(CARDS_PLAYED);
            int turnNumber = (int) context.getExtendedState().getVariables().get(TURN_NUMBER);
            int resultStage = cards.get(cards.size() - 2).compareTo(cards.get(cards.size() - 1));

            if (resultStage == 0 && player == context.getExtendedState().getVariables().get(HAND)) {
                context.getExtendedState().getVariables().put(DRAW_1, true);
                context.getExtendedState().getVariables().put(TURN, player);
                context.getExtendedState().getVariables().put(TURN_NUMBER, ++turnNumber);
                return true;
            } else if ((resultStage == 1 && player != context.getExtendedState().getVariables().get(TURN)) ||
                    resultStage == -1 && player == context.getExtendedState().getVariables().get(TURN)) {
                context.getExtendedState().getVariables().put(TURN, player);
                context.getExtendedState().getVariables().put(ROUND_1_WINNER, player);
                context.getExtendedState().getVariables().put(TURN_NUMBER, ++turnNumber);
                return true;
            } else {
                return false;
            }
        };
    }

    //Finish round 2, next player calculation
    @Bean
    public Guard<States, Events> player1NextTurn3() {
        return playerNextTurn3(PLAYER_1);
    }

    @Bean
    public Guard<States, Events> player2NextTurn3() {
        return playerNextTurn3(PLAYER_2);
    }

    private Guard<States, Events> playerNextTurn3(Players player) {
        return context -> {
            if (!(boolean) context.getExtendedState().getVariables().getOrDefault(GAME_ENDED, false)) {
                List<Card> cards = (ArrayList<Card>) context.getExtendedState().getVariables().get(CARDS_PLAYED);
                int turnNumber = (int) context.getExtendedState().getVariables().get(TURN_NUMBER);
                int resultStage = cards.get(cards.size() - 2).compareTo(cards.get(cards.size() - 1));

                if (resultStage == 0) {
                    context.getExtendedState().getVariables().put(DRAW_2, true);
                    if ((boolean) context.getExtendedState().getVariables().getOrDefault(DRAW_1, false) &&
                            player == context.getExtendedState().getVariables().get(HAND)) {
                        context.getExtendedState().getVariables().put(TURN, player);
                        context.getExtendedState().getVariables().put(TURN_NUMBER, ++turnNumber);
                        return true;
                    } else if (!(boolean) context.getExtendedState().getVariables().getOrDefault(DRAW_1, false)) {
                        //Winner found
                        context.getExtendedState().getVariables().put(GAME_ENDED, true);
                        context.getExtendedState().getVariables().put(WINNER, context.getExtendedState().getVariables().get(ROUND_1_WINNER));
                        calculateScores(context);
                        return false;
                    }
                } else if ((resultStage == 1 && player != context.getExtendedState().getVariables().get(TURN)) ||
                        resultStage == -1 && player == context.getExtendedState().getVariables().get(TURN)) {

                    context.getExtendedState().getVariables().put(ROUND_2_WINNER, player);

                    if ((boolean) context.getExtendedState().getVariables().getOrDefault(DRAW_1, false)) {
                        //Winner found
                        context.getExtendedState().getVariables().put(GAME_ENDED, true);
                        context.getExtendedState().getVariables().put(WINNER, player);
                        calculateScores(context);
                        return false;
                    }

                    if (context.getExtendedState().getVariables().get(ROUND_1_WINNER)
                            == context.getExtendedState().getVariables().get(ROUND_2_WINNER)) {
                        //Winner found
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

    //Finish round 3, winner calculation
    @Bean
    public Guard<States, Events> player1WinTurn3() {
        return playerWinTurn3(PLAYER_1);
    }

    @Bean
    public Guard<States, Events> player2WinTurn3() {
        return playerWinTurn3(PLAYER_2);
    }

    private Guard<States, Events> playerWinTurn3(Players player) {
        return new Guard<States, Events>() {
            @Override
            public boolean evaluate(StateContext<States, Events> context) {
                if (!(boolean) context.getExtendedState().getVariables().getOrDefault(GAME_ENDED, false)) {
                    List<Card> cards = (ArrayList<Card>) context.getExtendedState().getVariables().get(CARDS_PLAYED);
                    int resultStage = cards.get(cards.size() - 2).compareTo(cards.get(cards.size() - 1));

                    if (resultStage == 0) {
                        if ((boolean) context.getExtendedState().getVariables().getOrDefault(DRAW_1, false)
                                && (boolean) context.getExtendedState().getVariables().getOrDefault(DRAW_2, false)) {
                            context.getExtendedState().getVariables().put(WINNER, context.getExtendedState().getVariables().get(HAND));
                        } else {
                            context.getExtendedState().getVariables().put(WINNER, context.getExtendedState().getVariables().get(ROUND_1_WINNER));
                        }
                        context.getExtendedState().getVariables().put(DRAW_3, true);
                        context.getExtendedState().getVariables().put(GAME_ENDED, true);
                        calculateScores(context);
                        return true;
                    } else if ((resultStage == 1 && player != context.getExtendedState().getVariables().get(TURN)) ||
                            resultStage == -1 && player == context.getExtendedState().getVariables().get(TURN)) {
                        context.getExtendedState().getVariables().put(ROUND_3_WINNER, player);
                        context.getExtendedState().getVariables().put(WINNER, player);
                        context.getExtendedState().getVariables().put(GAME_ENDED, true);
                        calculateScores(context);
                        context.getExtendedState().getVariables().put(WINNER, null);
                        return true;
                    }
                }
                return false;
            }
        };

    }

    //TODO
    private void calculateScores(StateContext<States, Events> context) {
        //ExtendedStateVariables.PLAYER_1_SCORE
        //ExtendedStateVariables.PLAYER_2_SCORE
    }
}
