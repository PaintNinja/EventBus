package net.minecraftforge.eventbus.test;

import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CancellationTests {
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
}
