package f.l.truco;

import f.l.truco.machine.Event;
import f.l.truco.machine.State;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.statemachine.StateMachine;

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    private StateMachine<State, Event> stateMachine;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        stateMachine.getState().getId();
        Map<Object, Object> variables = stateMachine.getExtendedState().getVariables();
    }
}