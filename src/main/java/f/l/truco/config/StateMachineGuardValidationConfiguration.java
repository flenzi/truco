package f.l.truco.config;

import static f.l.truco.machine.ExtendedStateVariable.CARDS_PLAYED;
import static f.l.truco.machine.ExtendedStateVariable.PLAYER_1_CARDS_CURRENT;
import static f.l.truco.machine.ExtendedStateVariable.PLAYER_2_CARDS_CURRENT;
import static f.l.truco.machine.ExtendedStateVariable.TURN;
import static f.l.truco.machine.MessageHeader.PLAYED_CARD;
import static f.l.truco.model.Player.PLAYER_1;
import static f.l.truco.model.Player.PLAYER_2;

import f.l.truco.machine.Event;
import f.l.truco.machine.ExtendedStateVariable;
import f.l.truco.machine.State;
import f.l.truco.model.Card;
import f.l.truco.model.Player;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.guard.Guard;

@Configuration
public class StateMachineGuardValidationConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(StateMachineGuardValidationConfiguration.class);

    @Bean
    public Guard<State, Event> player1PlayCard() {
        return playerPlayCard(PLAYER_1, PLAYER_1_CARDS_CURRENT);
    }

    @Bean
    public Guard<State, Event> player2PlayCard() {
        return playerPlayCard(PLAYER_2, PLAYER_2_CARDS_CURRENT);
    }

    private Guard<State, Event> playerPlayCard(Player player, ExtendedStateVariable playerCards) {
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
