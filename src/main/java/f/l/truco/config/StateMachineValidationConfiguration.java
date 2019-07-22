package f.l.truco.config;

import f.l.truco.machine.Events;
import f.l.truco.machine.ExtendedStateVariables;
import f.l.truco.machine.States;
import f.l.truco.model.Card;
import f.l.truco.model.Players;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;

import java.util.List;

import static f.l.truco.machine.ExtendedStateVariables.CARDS_PLAYED;
import static f.l.truco.machine.ExtendedStateVariables.PLAYER_1_CARDS;
import static f.l.truco.machine.ExtendedStateVariables.PLAYER_2_CARDS;
import static f.l.truco.machine.ExtendedStateVariables.TURN;
import static f.l.truco.machine.MessageHeaders.PLAYED_CARD;
import static f.l.truco.model.Players.PLAYER_1;
import static f.l.truco.model.Players.PLAYER_2;

@Configuration
public class StateMachineValidationConfiguration {

    @Bean
    public Guard<States, Events> player1PlayCardValidate() {
        return playerPlayCardValidate(PLAYER_1, PLAYER_1_CARDS);
    }

    @Bean
    public Guard<States, Events> player2PlayCardValidate() {
        return playerPlayCardValidate(PLAYER_2, PLAYER_2_CARDS);
    }

    private Guard<States, Events> playerPlayCardValidate(Players player, ExtendedStateVariables playerCards) {
        return new Guard<States, Events>() {

            @Override
            public boolean evaluate(StateContext<States, Events> context) {
                Card playedCard = (Card) context.getMessage().getHeaders().get(PLAYED_CARD.toString());
                List<Card> cards = (List<Card>) context.getExtendedState().getVariables().get(playerCards);

                if (!cards.contains(playedCard)) {
                    throw new RuntimeException("Invalid card played");
                }

                if (player == context.getExtendedState().getVariables().get(TURN)) {
                    ((List<Card>) context.getExtendedState().getVariables().get(CARDS_PLAYED)).add(playedCard);
                    ((List<Card>) context.getExtendedState().getVariables().get(playerCards)).remove(playedCard);
                    return true;
                }

                return false;
            }
        };
    }

}
