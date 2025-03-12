package net.minecraftforge.eventbus.test;

import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

public class IndividualEventListenerTests {
    /**
     * Tests that a registered inline/anonymous lambda listener is called when the event is posted.
     */
    @Test
    public void testInlineLambdaListenersAreCalled() {
        record TestEvent() implements RecordEvent {
            static final EventBus<TestEvent> BUS = EventBus.create(TestEvent.class);
        }

        var wasCalled = new AtomicBoolean();
        TestEvent.BUS.addListener(event -> wasCalled.set(true));
        Assertions.assertFalse(wasCalled.get(), "Inline lambda listener should not have been called yet");
        TestEvent.BUS.post(new TestEvent());
        Assertions.assertTrue(wasCalled.get(), "Inline lambda listener should have been called");
    }

    /**
     * Tests that registered method reference listeners are called when the event is posted.
     */
    @Test
    public void testMethodReferenceListenersAreCalled() {
        record TestEvent() implements RecordEvent {
            static final EventBus<TestEvent> BUS = EventBus.create(TestEvent.class);
        }

        final class Listeners {
            static volatile boolean staticListenerCalled;
            static void staticListener(TestEvent event) {
                staticListenerCalled = true;
            }

            volatile boolean instanceListenerCalled;
            void instanceListener(TestEvent event) {
                instanceListenerCalled = true;
            }
        }

        TestEvent.BUS.addListener(Listeners::staticListener);
        Assertions.assertFalse(Listeners.staticListenerCalled, "Static listener should not have been called yet");
        TestEvent.BUS.post(new TestEvent());
        Assertions.assertTrue(Listeners.staticListenerCalled, "Static listener should have been called");

        var instance = new Listeners();
        TestEvent.BUS.addListener(instance::instanceListener);
        Assertions.assertFalse(instance.instanceListenerCalled, "Instance listener should not have been called yet");
        TestEvent.BUS.post(new TestEvent());
        Assertions.assertTrue(instance.instanceListenerCalled, "Instance listener should have been called");
    }
}
