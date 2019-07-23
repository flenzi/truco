package f.l.truco.config;

import f.l.truco.machine.Events;
import f.l.truco.machine.States;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.ArrayList;

import static f.l.truco.machine.Events.INITIALIZE_TEST;
import static f.l.truco.machine.ExtendedStateVariables.CARDS_PLAYED;
import static f.l.truco.machine.ExtendedStateVariables.HAND;
import static f.l.truco.machine.ExtendedStateVariables.PLAYER_1_CARDS;
import static f.l.truco.machine.ExtendedStateVariables.PLAYER_2_CARDS;
import static f.l.truco.machine.ExtendedStateVariables.TURN;
import static f.l.truco.machine.ExtendedStateVariables.TURN_NUMBER;
import static f.l.truco.model.Players.PLAYER_1;

@Configuration
@Import({StateMachineGuardValidationConfiguration.class, StateMachineListenersConfiguration.class})
public class StateMachineConfigurationTest extends StateMachineConfiguration {

    @Override
    public void configure(StateMachineTransitionConfigurer<States, Events> transitions)
            throws Exception {
        super.configure(transitions);

        // added extra event for test initialization with fixed cards
        transitions
                .withExternal()
                .source(States.INITIAL)
                .target(States.PLAYER_1_TURN_1)
                .event(INITIALIZE_TEST)
                .action(initialSetActionTest());
    }

    @Bean
    public Action<States, Events> initialSetActionTest() {
        return context -> {
            context.getExtendedState().getVariables()
                    .put(PLAYER_1_CARDS, context.getMessage().getHeaders().get("player1cards"));

            context.getExtendedState().getVariables()
                    .put(PLAYER_2_CARDS, context.getMessage().getHeaders().get("player2cards"));

            context.getExtendedState().getVariables().put(HAND, PLAYER_1);
            context.getExtendedState().getVariables().put(TURN, PLAYER_1);
            context.getExtendedState().getVariables().put(TURN_NUMBER, 1);
            context.getExtendedState().getVariables().put(CARDS_PLAYED, new ArrayList<>(6));
        };
    }

}
