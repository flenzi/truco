package f.l.truco.config;

import f.l.truco.machine.Event;
import f.l.truco.machine.State;
import f.l.truco.model.Card;
import f.l.truco.model.Player;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;

import java.util.ArrayList;
import java.util.List;

import static f.l.truco.machine.ExtendedStateVariable.*;
import static f.l.truco.model.Player.PLAYER_1;
import static f.l.truco.model.Player.PLAYER_2;

@Configuration
public class StateMachineRoundsConfiguration {

    @Bean
    public Guard<State, Event> player1NextTurnOdd() {
        return playerNextTurnOdd(PLAYER_1);
    }

    @Bean
    public Guard<State, Event> player2NextTurnOdd() {
        return playerNextTurnOdd(PLAYER_2);
    }

    private Guard<State, Event> playerNextTurnOdd(Player player) {
        return context -> {
            int turnNumber = (int) context.getExtendedState().getVariables().get(TURN_NUMBER);
            if (player != context.getExtendedState().getVariables().get(TURN) && turnNumber % 2 == 1) {
                context.getExtendedState().getVariables().put(TURN, player);
                context.getExtendedState().getVariables().put(TURN_NUMBER, ++turnNumber);
                return true;
            }
            return false;
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
        return context -> {
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
        };
    }

    private void calculateScores(StateContext<State, Event> context) {
        int additionalScore = 0;
        if (null != context.getExtendedState().getVariables().get(TRUCO_ASKED)) {
            additionalScore++;
        }
        if (null != context.getExtendedState().getVariables().get(RE_TRUCO_ASKED)) {
            additionalScore++;
        }
        if (null != context.getExtendedState().getVariables().get(VALE4_ASKED)) {
            additionalScore++;
        }

        Player player = (Player) context.getExtendedState().getVariables().get(WINNER);

        if (player == PLAYER_1) {
            int currentScore = (int) context.getExtendedState().getVariables().getOrDefault(PLAYER_1_SCORE, 0);
            int finalScore = currentScore + 1 + additionalScore;
            context.getExtendedState().getVariables().put(PLAYER_1_SCORE, finalScore);
        } else {
            int currentScore = (int) context.getExtendedState().getVariables().getOrDefault(PLAYER_2_SCORE, 0);
            int finalScore = currentScore + 1 + additionalScore;
            context.getExtendedState().getVariables().put(PLAYER_2_SCORE, finalScore);
        }
    }
}
