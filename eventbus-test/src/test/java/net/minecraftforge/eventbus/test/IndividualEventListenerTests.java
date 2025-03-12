package net.minecraftforge.eventbus.test;

import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;
import net.minecraftforge.eventbus.api.listener.EventListener;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

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

    /**
     * Tests that a listener can cancel an event.
     */
    @Test
    public void testCancellingListener() {
        record CancellableTestEvent() implements Cancellable, RecordEvent {
            static final CancellableEventBus<CancellableTestEvent> BUS = CancellableEventBus.create(CancellableTestEvent.class);
        }

        var listener = CancellableTestEvent.BUS.addListener(event -> true);

        var wasCancelled = CancellableTestEvent.BUS.post(new CancellableTestEvent());
        Assertions.assertTrue(wasCancelled, "The event should have been cancelled");

        CancellableTestEvent.BUS.removeListener(listener);
        wasCancelled = CancellableTestEvent.BUS.post(new CancellableTestEvent());
        Assertions.assertFalse(wasCancelled, "The event should not have been cancelled without any listeners");
    }

    /**
     * Tests that an always cancelling listener cancels the event.
     */
    @Test
    public void testAlwaysCancellingListener() {
        record AlwaysCancellingTestEvent() implements Cancellable, RecordEvent {
            static final CancellableEventBus<AlwaysCancellingTestEvent> BUS = CancellableEventBus.create(AlwaysCancellingTestEvent.class);
        }

        var listener = AlwaysCancellingTestEvent.BUS.addListener(true, event -> {});

        var wasCancelled = AlwaysCancellingTestEvent.BUS.post(new AlwaysCancellingTestEvent());
        Assertions.assertTrue(wasCancelled, "The event should have been cancelled");

        AlwaysCancellingTestEvent.BUS.removeListener(listener);
        wasCancelled = AlwaysCancellingTestEvent.BUS.post(new AlwaysCancellingTestEvent());
        Assertions.assertFalse(wasCancelled, "The event should not have been cancelled without any listeners");
    }

    /**
     * Tests that exceptions thrown by listeners are propagated to the poster.
     */
    @Test
    public void testListenerExceptionPropagation() {
        record ExceptionThrowingTestEvent() implements RecordEvent {
            static final EventBus<ExceptionThrowingTestEvent> BUS = EventBus.create(ExceptionThrowingTestEvent.class);
        }

        var exception = new RuntimeException("Test exception");
        var listener = ExceptionThrowingTestEvent.BUS.addListener(event -> {
            throw exception;
        });

        Assertions.assertThrows(
                RuntimeException.class,
                () -> ExceptionThrowingTestEvent.BUS.post(new ExceptionThrowingTestEvent()),
                "The exception thrown by the listener should have been propagated to the poster"
        );

        ExceptionThrowingTestEvent.BUS.removeListener(listener);
    }

    /**
     * Tests that events posted from listeners in other events works as expected.
     */
    @Test
    public void testEventPostingFromListener() {
        record EventA() implements RecordEvent {
            static final EventBus<EventA> BUS = EventBus.create(EventA.class);
        }

        record EventB() implements RecordEvent {
            static final EventBus<EventB> BUS = EventBus.create(EventB.class);
        }

        var aWasCalled = new AtomicBoolean();
        var bWasCalled = new AtomicBoolean();
        var eventAListener = EventA.BUS.addListener(event -> {
            EventB.BUS.post(new EventB());
            aWasCalled.set(true);
        });
        var eventBListener = EventB.BUS.addListener(event -> bWasCalled.set(true));

        Assertions.assertFalse(aWasCalled.get(), "EventA listener should not have been called yet");
        Assertions.assertFalse(bWasCalled.get(), "EventB listener should not have been called yet");

        EventA.BUS.post(new EventA());

        Assertions.assertTrue(aWasCalled.get(), "EventA listener should have been called");
        Assertions.assertTrue(bWasCalled.get(), "EventB listener should have been called");

        EventA.BUS.removeListener(eventAListener);
        EventB.BUS.removeListener(eventBListener);
    }

    /**
     * Tests that single use listeners work as expected (a listener that removes itself after being called once).
     */
    @Test
    public void testSingleUseListener() {
        record SingleUseTestEvent() implements RecordEvent {
            static final EventBus<SingleUseTestEvent> BUS = EventBus.create(SingleUseTestEvent.class);
        }

        var wasCalled = new AtomicBoolean();
        var listenerRef = new AtomicReference<EventListener>();
        var listener = SingleUseTestEvent.BUS.addListener(event -> {
            wasCalled.set(true);
            SingleUseTestEvent.BUS.removeListener(listenerRef.get());
        });
        listenerRef.set(listener);

        Assertions.assertFalse(wasCalled.get(), "Single use listener should not have been called yet");
        SingleUseTestEvent.BUS.post(new SingleUseTestEvent());
        Assertions.assertTrue(wasCalled.get(), "Single use listener should have been called");

        // The listener should have been removed after being called
        SingleUseTestEvent.BUS.post(new SingleUseTestEvent());
        Assertions.assertTrue(wasCalled.get(), "Single use listener should not have been called again");
    }
}
