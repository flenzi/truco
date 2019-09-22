package f.l.truco.config;

import f.l.truco.machine.Event;
import f.l.truco.machine.State;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.guard.Guard;

import static f.l.truco.machine.Event.*;
import static f.l.truco.machine.ExtendedStateVariable.*;
import static f.l.truco.model.Player.PLAYER_1;
import static f.l.truco.model.Player.PLAYER_2;

@Configuration
public class StateMachineTrucoConfiguration {

    @Bean
    public Guard<State, Event> trucoGuard() {
        return new Guard<State, Event>() {
            @Override
            public boolean evaluate(StateContext<State, Event> context) {
                return null == context.getExtendedState().getVariables().get(TRUCO_ASKED);
            }
        };
    }

    @Bean
    public Guard<State, Event> reTrucoGuard() {
        return new Guard<State, Event>() {
            @Override
            public boolean evaluate(StateContext<State, Event> context) {
                return null != context.getExtendedState().getVariables().get(TRUCO_ASKED) &&
                        null == context.getExtendedState().getVariables().get(RE_TRUCO_ASKED)
                        && ((context.getExtendedState().getVariables().get(TRUCO_ASKED) == PLAYER_1 && context.getEvent() == PLAYER_2_RE_TRUCO) ||
                        (context.getExtendedState().getVariables().get(TRUCO_ASKED) == PLAYER_2 && context.getEvent() == PLAYER_1_RE_TRUCO));
            }
        };
    }

    @Bean
    public Guard<State, Event> vale4Guard() {
        return new Guard<State, Event>() {
            @Override
            public boolean evaluate(StateContext<State, Event> context) {
                return null != context.getExtendedState().getVariables().get(RE_TRUCO_ASKED) &&
                        null == context.getExtendedState().getVariables().get(VALE4_ASKED)
                        && ((context.getExtendedState().getVariables().get(RE_TRUCO_ASKED) == PLAYER_1 && context.getEvent() == PLAYER_2_VALE_4) ||
                        (context.getExtendedState().getVariables().get(RE_TRUCO_ASKED) == PLAYER_2 && context.getEvent() == PLAYER_1_VALE_4));
            }
        };
    }


    @Bean
    public Action<State, Event> trucoAction() {
        return new Action<State, Event>() {
            @Override
            public void execute(StateContext<State, Event> context) {
                if (Event.PLAYER_1_TRUCO == context.getEvent()) {
                    context.getExtendedState().getVariables().put(TRUCO_ASKED, PLAYER_1);
                } else if (Event.PLAYER_2_TRUCO == context.getEvent()) {
                    context.getExtendedState().getVariables().put(TRUCO_ASKED, PLAYER_2);
                }
            }
        };
    }

    @Bean
    public Action<State, Event> reTrucoAction() {
        return new Action<State, Event>() {
            @Override
            public void execute(StateContext<State, Event> context) {
                if (Event.PLAYER_1_RE_TRUCO == context.getEvent()) {
                    context.getExtendedState().getVariables().put(RE_TRUCO_ASKED, PLAYER_1);
                } else if (Event.PLAYER_2_RE_TRUCO == context.getEvent()) {
                    context.getExtendedState().getVariables().put(RE_TRUCO_ASKED, PLAYER_2);
                }
            }
        };
    }

    @Bean
    public Action<State, Event> vale4Action() {
        return new Action<State, Event>() {
            @Override
            public void execute(StateContext<State, Event> context) {
                if (Event.PLAYER_1_VALE_4 == context.getEvent()) {
                    context.getExtendedState().getVariables().put(VALE4_ASKED, PLAYER_1);
                } else if (Event.PLAYER_2_VALE_4 == context.getEvent()) {
                    context.getExtendedState().getVariables().put(VALE4_ASKED, PLAYER_2);
                }
            }
        };
    }


    //Actions for no decisions related to truco/retruco/vale4
    @Bean
    public Action<State, Event> playerNoTruco() {
        return playerNoCalculator(1);
    }

    @Bean
    public Action<State, Event> playerNoReTruco() {
        return playerNoCalculator(2);
    }

    @Bean
    public Action<State, Event> playerNoVale4() {
        return playerNoCalculator(3);
    }

    public Action<State, Event> playerNoCalculator(int value) {
        return context -> {
            if (Event.PLAYER_1_NO_TRUCO == context.getEvent() || Event.PLAYER_1_NO_RETRUCO == context.getEvent()
                    || PLAYER_1_NO_VALE4 == context.getEvent()) {
                int currentScore = (int) context.getExtendedState().getVariables().getOrDefault(PLAYER_2_SCORE, 0);
                context.getExtendedState().getVariables().put(PLAYER_2_SCORE, currentScore + value);
                context.getExtendedState().getVariables().put(WINNER, PLAYER_2);
            } else if (Event.PLAYER_2_NO_TRUCO == context.getEvent() || Event.PLAYER_2_NO_RETRUCO == context.getEvent()
                    || PLAYER_2_NO_VALE4 == context.getEvent()) {
                int currentScore = (int) context.getExtendedState().getVariables().getOrDefault(PLAYER_1_SCORE, 0);
                context.getExtendedState().getVariables().put(PLAYER_1_SCORE, currentScore + value);
                context.getExtendedState().getVariables().put(WINNER, PLAYER_1);
            }
        };
    }
}
