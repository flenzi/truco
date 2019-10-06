package f.l.truco.config;

import f.l.truco.machine.Event;
import f.l.truco.machine.State;
import f.l.truco.model.Card;
import f.l.truco.model.Player;
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

import java.util.*;

import static f.l.truco.machine.ExtendedStateVariable.*;
import static f.l.truco.machine.State.getNextStateAfterYesDecision;
import static f.l.truco.model.Player.PLAYER_1;
import static f.l.truco.model.Player.PLAYER_2;

@Configuration
@EnableStateMachine
public class StateMachineConfiguration extends EnumStateMachineConfigurerAdapter<State, Event> {

    @Autowired
    private Guard<State, Event> player1PlayCard;

    @Autowired
    private Guard<State, Event> player2PlayCard;

    @Autowired
    private Guard<State, Event> trucoGuard;

    @Autowired
    private Action<State, Event> trucoAction;

    @Autowired
    private Guard<State, Event> reTrucoGuard;

    @Autowired
    private Action<State, Event> reTrucoAction;

    @Autowired
    private Guard<State, Event> vale4Guard;

    @Autowired
    private Action<State, Event> vale4Action;

    @Autowired
    private Action<State, Event> playerNoTruco;

    @Autowired
    private Action<State, Event> playerNoReTruco;

    @Autowired
    private Action<State, Event> playerNoVale4;

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
        setTrucoTransitions(transitions);
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

    private void setTrucoTransitions(StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {
        //Truco
        setTrucoTransitionByTurn(transitions, State.PLAYER_1_TURN_1, Event.PLAYER_1_TRUCO, State.PLAYER_2_DECISION_TURN_1_PLAYER_1);
        setTrucoTransitionByTurn(transitions, State.PLAYER_1_TURN_2, Event.PLAYER_1_TRUCO, State.PLAYER_2_DECISION_TURN_2_PLAYER_1);
        setTrucoTransitionByTurn(transitions, State.PLAYER_1_TURN_3, Event.PLAYER_1_TRUCO, State.PLAYER_2_DECISION_TURN_3_PLAYER_1);
        setTrucoTransitionByTurn(transitions, State.PLAYER_1_TURN_1, Event.PLAYER_2_TRUCO, State.PLAYER_1_DECISION_TURN_1_PLAYER_1);
        setTrucoTransitionByTurn(transitions, State.PLAYER_1_TURN_2, Event.PLAYER_2_TRUCO, State.PLAYER_1_DECISION_TURN_2_PLAYER_1);
        setTrucoTransitionByTurn(transitions, State.PLAYER_1_TURN_3, Event.PLAYER_2_TRUCO, State.PLAYER_1_DECISION_TURN_3_PLAYER_1);

        setTrucoTransitionByTurn(transitions, State.PLAYER_2_TURN_1, Event.PLAYER_2_TRUCO, State.PLAYER_1_DECISION_TURN_1_PLAYER_2);
        setTrucoTransitionByTurn(transitions, State.PLAYER_2_TURN_2, Event.PLAYER_2_TRUCO, State.PLAYER_1_DECISION_TURN_2_PLAYER_2);
        setTrucoTransitionByTurn(transitions, State.PLAYER_2_TURN_3, Event.PLAYER_2_TRUCO, State.PLAYER_1_DECISION_TURN_3_PLAYER_2);
        setTrucoTransitionByTurn(transitions, State.PLAYER_2_TURN_1, Event.PLAYER_1_TRUCO, State.PLAYER_2_DECISION_TURN_1_PLAYER_2);
        setTrucoTransitionByTurn(transitions, State.PLAYER_2_TURN_2, Event.PLAYER_1_TRUCO, State.PLAYER_2_DECISION_TURN_2_PLAYER_2);
        setTrucoTransitionByTurn(transitions, State.PLAYER_2_TURN_3, Event.PLAYER_1_TRUCO, State.PLAYER_2_DECISION_TURN_3_PLAYER_2);

        //Yes decision
        setTransitionYesDecision(transitions, State.PLAYER_1_DECISION_TURN_1_PLAYER_1, Event.PLAYER_1_YES_TRUCO);
        setTransitionYesDecision(transitions, State.PLAYER_1_DECISION_TURN_2_PLAYER_1, Event.PLAYER_1_YES_TRUCO);
        setTransitionYesDecision(transitions, State.PLAYER_1_DECISION_TURN_3_PLAYER_1, Event.PLAYER_1_YES_TRUCO);
        setTransitionYesDecision(transitions, State.PLAYER_2_DECISION_TURN_1_PLAYER_1, Event.PLAYER_2_YES_TRUCO);
        setTransitionYesDecision(transitions, State.PLAYER_2_DECISION_TURN_2_PLAYER_1, Event.PLAYER_2_YES_TRUCO);
        setTransitionYesDecision(transitions, State.PLAYER_2_DECISION_TURN_3_PLAYER_1, Event.PLAYER_2_YES_TRUCO);

        setTransitionYesDecision(transitions, State.PLAYER_1_DECISION_TURN_1_PLAYER_2, Event.PLAYER_1_YES_TRUCO);
        setTransitionYesDecision(transitions, State.PLAYER_1_DECISION_TURN_2_PLAYER_2, Event.PLAYER_1_YES_TRUCO);
        setTransitionYesDecision(transitions, State.PLAYER_1_DECISION_TURN_3_PLAYER_2, Event.PLAYER_1_YES_TRUCO);
        setTransitionYesDecision(transitions, State.PLAYER_2_DECISION_TURN_1_PLAYER_2, Event.PLAYER_2_YES_TRUCO);
        setTransitionYesDecision(transitions, State.PLAYER_2_DECISION_TURN_2_PLAYER_2, Event.PLAYER_2_YES_TRUCO);
        setTransitionYesDecision(transitions, State.PLAYER_2_DECISION_TURN_3_PLAYER_2, Event.PLAYER_2_YES_TRUCO);

        //No decision
        setTransitionNoDecision(transitions, State.PLAYER_1_DECISION_TURN_1_PLAYER_1, Event.PLAYER_1_NO_TRUCO, playerNoTruco);
        setTransitionNoDecision(transitions, State.PLAYER_1_DECISION_TURN_2_PLAYER_1, Event.PLAYER_1_NO_TRUCO, playerNoTruco);
        setTransitionNoDecision(transitions, State.PLAYER_1_DECISION_TURN_3_PLAYER_1, Event.PLAYER_1_NO_TRUCO, playerNoTruco);
        setTransitionNoDecision(transitions, State.PLAYER_2_DECISION_TURN_1_PLAYER_1, Event.PLAYER_2_NO_TRUCO, playerNoTruco);
        setTransitionNoDecision(transitions, State.PLAYER_2_DECISION_TURN_2_PLAYER_1, Event.PLAYER_2_NO_TRUCO, playerNoTruco);
        setTransitionNoDecision(transitions, State.PLAYER_2_DECISION_TURN_3_PLAYER_1, Event.PLAYER_2_NO_TRUCO, playerNoTruco);

        setTransitionNoDecision(transitions, State.PLAYER_1_DECISION_TURN_1_PLAYER_2, Event.PLAYER_1_NO_TRUCO, playerNoTruco);
        setTransitionNoDecision(transitions, State.PLAYER_1_DECISION_TURN_2_PLAYER_2, Event.PLAYER_1_NO_TRUCO, playerNoTruco);
        setTransitionNoDecision(transitions, State.PLAYER_1_DECISION_TURN_3_PLAYER_2, Event.PLAYER_1_NO_TRUCO, playerNoTruco);
        setTransitionNoDecision(transitions, State.PLAYER_2_DECISION_TURN_1_PLAYER_2, Event.PLAYER_2_NO_TRUCO, playerNoTruco);
        setTransitionNoDecision(transitions, State.PLAYER_2_DECISION_TURN_2_PLAYER_2, Event.PLAYER_2_NO_TRUCO, playerNoTruco);
        setTransitionNoDecision(transitions, State.PLAYER_2_DECISION_TURN_3_PLAYER_2, Event.PLAYER_2_NO_TRUCO, playerNoTruco);

        //ReTruco
        setReTrucoTransitionByTurn(transitions, State.PLAYER_1_TURN_1, Event.PLAYER_1_RE_TRUCO, State.PLAYER_2_DECISION_TURN_1_PLAYER_1);
        setReTrucoTransitionByTurn(transitions, State.PLAYER_1_TURN_2, Event.PLAYER_1_RE_TRUCO, State.PLAYER_2_DECISION_TURN_2_PLAYER_1);
        setReTrucoTransitionByTurn(transitions, State.PLAYER_1_TURN_3, Event.PLAYER_1_RE_TRUCO, State.PLAYER_2_DECISION_TURN_3_PLAYER_1);
        setReTrucoTransitionByTurn(transitions, State.PLAYER_1_TURN_1, Event.PLAYER_2_RE_TRUCO, State.PLAYER_1_DECISION_TURN_1_PLAYER_1);
        setReTrucoTransitionByTurn(transitions, State.PLAYER_1_TURN_2, Event.PLAYER_2_RE_TRUCO, State.PLAYER_1_DECISION_TURN_2_PLAYER_1);
        setReTrucoTransitionByTurn(transitions, State.PLAYER_1_TURN_3, Event.PLAYER_2_RE_TRUCO, State.PLAYER_1_DECISION_TURN_3_PLAYER_1);

        setReTrucoTransitionByTurn(transitions, State.PLAYER_2_TURN_1, Event.PLAYER_2_RE_TRUCO, State.PLAYER_1_DECISION_TURN_1_PLAYER_2);
        setReTrucoTransitionByTurn(transitions, State.PLAYER_2_TURN_2, Event.PLAYER_2_RE_TRUCO, State.PLAYER_1_DECISION_TURN_2_PLAYER_2);
        setReTrucoTransitionByTurn(transitions, State.PLAYER_2_TURN_3, Event.PLAYER_2_RE_TRUCO, State.PLAYER_1_DECISION_TURN_3_PLAYER_2);
        setReTrucoTransitionByTurn(transitions, State.PLAYER_2_TURN_1, Event.PLAYER_1_RE_TRUCO, State.PLAYER_2_DECISION_TURN_1_PLAYER_2);
        setReTrucoTransitionByTurn(transitions, State.PLAYER_2_TURN_2, Event.PLAYER_1_RE_TRUCO, State.PLAYER_2_DECISION_TURN_2_PLAYER_2);
        setReTrucoTransitionByTurn(transitions, State.PLAYER_2_TURN_3, Event.PLAYER_1_RE_TRUCO, State.PLAYER_2_DECISION_TURN_3_PLAYER_2);

        //Yes decision
        setTransitionYesDecision(transitions, State.PLAYER_1_DECISION_TURN_1_PLAYER_1, Event.PLAYER_1_YES_RETRUCO);
        setTransitionYesDecision(transitions, State.PLAYER_1_DECISION_TURN_2_PLAYER_1, Event.PLAYER_1_YES_RETRUCO);
        setTransitionYesDecision(transitions, State.PLAYER_1_DECISION_TURN_3_PLAYER_1, Event.PLAYER_1_YES_RETRUCO);
        setTransitionYesDecision(transitions, State.PLAYER_2_DECISION_TURN_1_PLAYER_1, Event.PLAYER_2_YES_RETRUCO);
        setTransitionYesDecision(transitions, State.PLAYER_2_DECISION_TURN_2_PLAYER_1, Event.PLAYER_2_YES_RETRUCO);
        setTransitionYesDecision(transitions, State.PLAYER_2_DECISION_TURN_3_PLAYER_1, Event.PLAYER_2_YES_RETRUCO);

        setTransitionYesDecision(transitions, State.PLAYER_1_DECISION_TURN_1_PLAYER_2, Event.PLAYER_1_YES_RETRUCO);
        setTransitionYesDecision(transitions, State.PLAYER_1_DECISION_TURN_2_PLAYER_2, Event.PLAYER_1_YES_RETRUCO);
        setTransitionYesDecision(transitions, State.PLAYER_1_DECISION_TURN_3_PLAYER_2, Event.PLAYER_1_YES_RETRUCO);
        setTransitionYesDecision(transitions, State.PLAYER_2_DECISION_TURN_1_PLAYER_2, Event.PLAYER_2_YES_RETRUCO);
        setTransitionYesDecision(transitions, State.PLAYER_2_DECISION_TURN_2_PLAYER_2, Event.PLAYER_2_YES_RETRUCO);
        setTransitionYesDecision(transitions, State.PLAYER_2_DECISION_TURN_3_PLAYER_2, Event.PLAYER_2_YES_RETRUCO);

        //No decision
        setTransitionNoDecision(transitions, State.PLAYER_1_DECISION_TURN_1_PLAYER_1, Event.PLAYER_1_NO_RETRUCO, playerNoReTruco);
        setTransitionNoDecision(transitions, State.PLAYER_1_DECISION_TURN_2_PLAYER_1, Event.PLAYER_1_NO_RETRUCO, playerNoReTruco);
        setTransitionNoDecision(transitions, State.PLAYER_1_DECISION_TURN_3_PLAYER_1, Event.PLAYER_1_NO_RETRUCO, playerNoReTruco);
        setTransitionNoDecision(transitions, State.PLAYER_2_DECISION_TURN_1_PLAYER_1, Event.PLAYER_2_NO_RETRUCO, playerNoReTruco);
        setTransitionNoDecision(transitions, State.PLAYER_2_DECISION_TURN_2_PLAYER_1, Event.PLAYER_2_NO_RETRUCO, playerNoReTruco);
        setTransitionNoDecision(transitions, State.PLAYER_2_DECISION_TURN_3_PLAYER_1, Event.PLAYER_2_NO_RETRUCO, playerNoReTruco);

        setTransitionNoDecision(transitions, State.PLAYER_1_DECISION_TURN_1_PLAYER_2, Event.PLAYER_1_NO_RETRUCO, playerNoReTruco);
        setTransitionNoDecision(transitions, State.PLAYER_1_DECISION_TURN_2_PLAYER_2, Event.PLAYER_1_NO_RETRUCO, playerNoReTruco);
        setTransitionNoDecision(transitions, State.PLAYER_1_DECISION_TURN_3_PLAYER_2, Event.PLAYER_1_NO_RETRUCO, playerNoReTruco);
        setTransitionNoDecision(transitions, State.PLAYER_2_DECISION_TURN_1_PLAYER_2, Event.PLAYER_2_NO_RETRUCO, playerNoReTruco);
        setTransitionNoDecision(transitions, State.PLAYER_2_DECISION_TURN_2_PLAYER_2, Event.PLAYER_2_NO_RETRUCO, playerNoReTruco);
        setTransitionNoDecision(transitions, State.PLAYER_2_DECISION_TURN_3_PLAYER_2, Event.PLAYER_2_NO_RETRUCO, playerNoReTruco);


        // Vale4
        setVale4TransitionByTurn(transitions, State.PLAYER_1_TURN_1, Event.PLAYER_1_VALE_4, State.PLAYER_2_DECISION_TURN_1_PLAYER_1);
        setVale4TransitionByTurn(transitions, State.PLAYER_1_TURN_2, Event.PLAYER_1_VALE_4, State.PLAYER_2_DECISION_TURN_2_PLAYER_1);
        setVale4TransitionByTurn(transitions, State.PLAYER_1_TURN_3, Event.PLAYER_1_VALE_4, State.PLAYER_2_DECISION_TURN_3_PLAYER_1);
        setVale4TransitionByTurn(transitions, State.PLAYER_1_TURN_1, Event.PLAYER_2_VALE_4, State.PLAYER_1_DECISION_TURN_1_PLAYER_1);
        setVale4TransitionByTurn(transitions, State.PLAYER_1_TURN_2, Event.PLAYER_2_VALE_4, State.PLAYER_1_DECISION_TURN_2_PLAYER_1);
        setVale4TransitionByTurn(transitions, State.PLAYER_1_TURN_3, Event.PLAYER_2_VALE_4, State.PLAYER_1_DECISION_TURN_3_PLAYER_1);

        setReTrucoTransitionByTurn(transitions, State.PLAYER_2_TURN_1, Event.PLAYER_2_VALE_4, State.PLAYER_1_DECISION_TURN_1_PLAYER_2);
        setReTrucoTransitionByTurn(transitions, State.PLAYER_2_TURN_2, Event.PLAYER_2_VALE_4, State.PLAYER_1_DECISION_TURN_2_PLAYER_2);
        setReTrucoTransitionByTurn(transitions, State.PLAYER_2_TURN_3, Event.PLAYER_2_VALE_4, State.PLAYER_1_DECISION_TURN_3_PLAYER_2);
        setReTrucoTransitionByTurn(transitions, State.PLAYER_2_TURN_1, Event.PLAYER_1_VALE_4, State.PLAYER_2_DECISION_TURN_1_PLAYER_2);
        setReTrucoTransitionByTurn(transitions, State.PLAYER_2_TURN_2, Event.PLAYER_1_VALE_4, State.PLAYER_2_DECISION_TURN_2_PLAYER_2);
        setReTrucoTransitionByTurn(transitions, State.PLAYER_2_TURN_3, Event.PLAYER_1_VALE_4, State.PLAYER_2_DECISION_TURN_3_PLAYER_2);

        //Yes decision
        setTransitionYesDecision(transitions, State.PLAYER_1_DECISION_TURN_1_PLAYER_1, Event.PLAYER_1_YES_VALE4);
        setTransitionYesDecision(transitions, State.PLAYER_1_DECISION_TURN_2_PLAYER_1, Event.PLAYER_1_YES_VALE4);
        setTransitionYesDecision(transitions, State.PLAYER_1_DECISION_TURN_3_PLAYER_1, Event.PLAYER_1_YES_VALE4);
        setTransitionYesDecision(transitions, State.PLAYER_2_DECISION_TURN_1_PLAYER_1, Event.PLAYER_2_YES_VALE4);
        setTransitionYesDecision(transitions, State.PLAYER_2_DECISION_TURN_2_PLAYER_1, Event.PLAYER_2_YES_VALE4);
        setTransitionYesDecision(transitions, State.PLAYER_2_DECISION_TURN_3_PLAYER_1, Event.PLAYER_2_YES_VALE4);

        setTransitionYesDecision(transitions, State.PLAYER_1_DECISION_TURN_1_PLAYER_2, Event.PLAYER_1_YES_VALE4);
        setTransitionYesDecision(transitions, State.PLAYER_1_DECISION_TURN_2_PLAYER_2, Event.PLAYER_1_YES_VALE4);
        setTransitionYesDecision(transitions, State.PLAYER_1_DECISION_TURN_3_PLAYER_2, Event.PLAYER_1_YES_VALE4);
        setTransitionYesDecision(transitions, State.PLAYER_2_DECISION_TURN_1_PLAYER_2, Event.PLAYER_2_YES_VALE4);
        setTransitionYesDecision(transitions, State.PLAYER_2_DECISION_TURN_2_PLAYER_2, Event.PLAYER_2_YES_VALE4);
        setTransitionYesDecision(transitions, State.PLAYER_2_DECISION_TURN_3_PLAYER_2, Event.PLAYER_2_YES_VALE4);

        //No decision
        setTransitionNoDecision(transitions, State.PLAYER_1_DECISION_TURN_1_PLAYER_1, Event.PLAYER_1_NO_VALE4, playerNoVale4);
        setTransitionNoDecision(transitions, State.PLAYER_1_DECISION_TURN_2_PLAYER_1, Event.PLAYER_1_NO_VALE4, playerNoVale4);
        setTransitionNoDecision(transitions, State.PLAYER_1_DECISION_TURN_3_PLAYER_1, Event.PLAYER_1_NO_VALE4, playerNoVale4);
        setTransitionNoDecision(transitions, State.PLAYER_2_DECISION_TURN_1_PLAYER_1, Event.PLAYER_2_NO_VALE4, playerNoVale4);
        setTransitionNoDecision(transitions, State.PLAYER_2_DECISION_TURN_2_PLAYER_1, Event.PLAYER_2_NO_VALE4, playerNoVale4);
        setTransitionNoDecision(transitions, State.PLAYER_2_DECISION_TURN_3_PLAYER_1, Event.PLAYER_2_NO_VALE4, playerNoVale4);

        setTransitionNoDecision(transitions, State.PLAYER_1_DECISION_TURN_1_PLAYER_2, Event.PLAYER_1_NO_VALE4, playerNoVale4);
        setTransitionNoDecision(transitions, State.PLAYER_1_DECISION_TURN_2_PLAYER_2, Event.PLAYER_1_NO_VALE4, playerNoVale4);
        setTransitionNoDecision(transitions, State.PLAYER_1_DECISION_TURN_3_PLAYER_2, Event.PLAYER_1_NO_VALE4, playerNoVale4);
        setTransitionNoDecision(transitions, State.PLAYER_2_DECISION_TURN_1_PLAYER_2, Event.PLAYER_2_NO_VALE4, playerNoVale4);
        setTransitionNoDecision(transitions, State.PLAYER_2_DECISION_TURN_2_PLAYER_2, Event.PLAYER_2_NO_VALE4, playerNoVale4);
        setTransitionNoDecision(transitions, State.PLAYER_2_DECISION_TURN_3_PLAYER_2, Event.PLAYER_2_NO_VALE4, playerNoVale4);
    }


    private void setTrucoTransitionByTurn(StateMachineTransitionConfigurer<State, Event> transitions, State currentPlayerTurn,
                                          Event currentPlayerTruco, State anotherPlayerDecisionTurn) throws Exception {
        setSingTransitionByTurn(transitions, currentPlayerTurn, currentPlayerTruco, anotherPlayerDecisionTurn, trucoGuard, trucoAction);
    }

    private void setReTrucoTransitionByTurn(StateMachineTransitionConfigurer<State, Event> transitions, State currentPlayerTurn,
                                            Event currentPlayerTruco, State anotherPlayerDecisionTurn) throws Exception {
        setSingTransitionByTurn(transitions, currentPlayerTurn, currentPlayerTruco, anotherPlayerDecisionTurn, reTrucoGuard, reTrucoAction);
    }

    private void setVale4TransitionByTurn(StateMachineTransitionConfigurer<State, Event> transitions, State currentPlayerTurn,
                                          Event currentPlayerTruco, State anotherPlayerDecisionTurn) throws Exception {
        setSingTransitionByTurn(transitions, currentPlayerTurn, currentPlayerTruco, anotherPlayerDecisionTurn, vale4Guard, vale4Action);
    }


    private void setSingTransitionByTurn(StateMachineTransitionConfigurer<State, Event> transitions, State currentPlayerTurn,
                                         Event currentPlayerTruco, State anotherPlayerDecisionTurn, Guard<State, Event> singGuard, Action<State,
            Event> singAction) throws Exception {
        transitions
                .withExternal()
                .source(currentPlayerTurn)
                .target(anotherPlayerDecisionTurn)
                .event(currentPlayerTruco)
                .guard(singGuard)
                .action(singAction);
    }


    private void setTransitionYesDecision(StateMachineTransitionConfigurer<State, Event> transitions, State playerDecisionTurn,
                                          Event playerYesDecision) throws Exception {
        transitions
                .withExternal()
                .source(playerDecisionTurn)
                .event(playerYesDecision)
                .target(getNextStateAfterYesDecision(playerDecisionTurn));
    }

    private void setTransitionNoDecision(StateMachineTransitionConfigurer<State, Event> transitions, State playerDecisionTurn,
                                         Event playerNoDecision, Action<State, Event> playerNoAction) throws Exception {
        transitions
                .withExternal()
                .source(playerDecisionTurn)
                .target(State.FINAL)
                .event(playerNoDecision)
                .action(playerNoAction);
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
