package f.l.truco.config;

import f.l.truco.machine.Events;
import f.l.truco.machine.ExtendedStateVariables;
import f.l.truco.machine.States;
import f.l.truco.model.Card;
import f.l.truco.model.Players;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
public class StateMachineGuardValidationConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(StateMachineGuardValidationConfiguration.class);

    @Bean
    public Guard<States, Events> player1PlayCard() {
        return playerPlayCard(PLAYER_1, PLAYER_1_CARDS);
    }

    @Bean
    public Guard<States, Events> player2PlayCard() {
        return playerPlayCard(PLAYER_2, PLAYER_2_CARDS);
    }

    private Guard<States, Events> playerPlayCard(Players player, ExtendedStateVariables playerCards) {
        return context -> {
            Card playedCard = (Card) context.getMessage().getHeaders().get(PLAYED_CARD.toString());
            List<Card> cards = (List<Card>) context.getExtendedState().getVariables().get(playerCards);

            if (player == context.getExtendedState().getVariables().get(TURN) && cards.contains(playedCard)) {
                ((List<Card>) context.getExtendedState().getVariables().get(CARDS_PLAYED)).add(playedCard);
                ((List<Card>) context.getExtendedState().getVariables().get(playerCards)).remove(playedCard);
                return true;
            }
            LOG.error("Invalid card played {}", playedCard);
            return false;
        };
    }

}
