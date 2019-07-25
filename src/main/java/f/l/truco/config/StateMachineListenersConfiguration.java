package f.l.truco.config;

import f.l.truco.machine.Event;
import f.l.truco.machine.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;

@Configuration
public class StateMachineListenersConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(StateMachineConfiguration.class);

    @Bean
    public StateMachineListener<State, Event> stateChangeListener() {
        return new StateMachineListenerAdapter<State, Event>() {
            @Override
            public void stateChanged(org.springframework.statemachine.state.State from, org.springframework.statemachine.state.State to) {
                LOG.info("State change to " + to.getId());
            }
        };
    }
}
