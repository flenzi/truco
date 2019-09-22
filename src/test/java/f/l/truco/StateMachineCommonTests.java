package f.l.truco;

import f.l.truco.config.StateMachineConfigurationTest;
import f.l.truco.machine.Event;
import f.l.truco.machine.State;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = StateMachineConfigurationTest.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public abstract class StateMachineCommonTests {

    private static final Logger LOG = LoggerFactory.getLogger(StateMachineRoundTest.class);

    @Rule
    public TestRule watcher = new TestWatcher() {
        protected void starting(Description description) {
            LOG.info("Starting test: " + description.getMethodName());
        }
    };

    @Autowired
    protected StateMachine<State, Event> stateMachine;

}
