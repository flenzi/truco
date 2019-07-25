package f.l.truco.config;

import f.l.truco.machine.Event;
import f.l.truco.machine.State;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.ArrayList;

import static f.l.truco.machine.Event.INITIALIZE_TEST;
import static f.l.truco.machine.ExtendedStateVariable.CARDS_PLAYED;
import static f.l.truco.machine.ExtendedStateVariable.HAND;
import static f.l.truco.machine.ExtendedStateVariable.PLAYER_1_CARDS_CURRENT;
import static f.l.truco.machine.ExtendedStateVariable.PLAYER_1_SCORE;
import static f.l.truco.machine.ExtendedStateVariable.PLAYER_2_CARDS_CURRENT;
import static f.l.truco.machine.ExtendedStateVariable.PLAYER_2_SCORE;
import static f.l.truco.machine.ExtendedStateVariable.TURN;
import static f.l.truco.machine.ExtendedStateVariable.TURN_NUMBER;
import static f.l.truco.model.Player.PLAYER_1;

@Configuration
@Import({StateMachineGuardValidationConfiguration.class, StateMachineListenersConfiguration.class})
public class StateMachineConfigurationTest extends StateMachineConfiguration {

    @Override
    public void configure(StateMachineTransitionConfigurer<State, Event> transitions)
            throws Exception {
        super.configure(transitions);

        // added extra event for test initialization with fixed cards
        transitions
                .withExternal()
                .source(State.INITIAL)
                .target(State.PLAYER_1_TURN_1)
                .event(INITIALIZE_TEST)
                .action(initialSetActionTest());
    }

    @Bean
    public Action<State, Event> initialSetActionTest() {
        return context -> {
            context.getExtendedState().getVariables()
                    .put(PLAYER_1_CARDS_CURRENT, context.getMessage().getHeaders().get("player1cards"));

            context.getExtendedState().getVariables()
                    .put(PLAYER_2_CARDS_CURRENT, context.getMessage().getHeaders().get("player2cards"));

            context.getExtendedState().getVariables().put(PLAYER_1_SCORE, 0);
            context.getExtendedState().getVariables().put(PLAYER_2_SCORE, 0);
            context.getExtendedState().getVariables().put(HAND, PLAYER_1);
            context.getExtendedState().getVariables().put(TURN, PLAYER_1);
            context.getExtendedState().getVariables().put(TURN_NUMBER, 1);
            context.getExtendedState().getVariables().put(CARDS_PLAYED, new ArrayList<>(6));
        };
    }

}
