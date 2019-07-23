package f.l.truco.config;

import f.l.truco.machine.Events;
import f.l.truco.machine.States;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

@Configuration
public class StateMachineListenersConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(StateMachineConfiguration.class);

    @Bean
    public StateMachineListener<States, Events> stateChangeListener() {
        return new StateMachineListenerAdapter<States, Events>() {
            @Override
            public void stateChanged(State<States, Events> from, State<States, Events> to) {
                LOG.info("State change to " + to.getId());
            }
        };
    }
}
