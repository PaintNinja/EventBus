package net.minecraftforge.eventbus.api.bus;

import net.minecraftforge.eventbus.api.event.Event;
import net.minecraftforge.eventbus.api.event.EventCharacteristic;
import net.minecraftforge.eventbus.api.listener.EventListener;
import net.minecraftforge.eventbus.api.listener.ObjBooleanBiConsumer;
import net.minecraftforge.eventbus.api.listener.Priority;
import net.minecraftforge.eventbus.internal.CancellableEventBusImpl;

import java.util.function.Consumer;
import java.util.function.Predicate;

public sealed interface CancellableEventBus<T extends Event<T> & EventCharacteristic.Cancellable>
        extends EventBus<T> permits CancellableEventBusImpl {
    /**
     * Adds an always cancelling listener to this EventBus with the default priority of {@link Priority#NORMAL}.
     * @param alwaysCancelling If true, always cancel the event after calling the listener. This acts as if you
     *                         added a Predicate listener that always returns true, but with additional optimisations.
     *                         <p>If false, you should use {@link #addListener(Consumer)} instead to avoid unnecessary
     *                         breaking changes if the event is no longer cancellable in the future.</p>
     * @param listener The listener to add.
     * @return A reference that can be used to remove this listener later with {@link #removeListener(EventListener)}.
     * @see #addListener(Predicate) For adding a listener that can cancel the event conditionally
     * @see #addListener(Consumer) For adding a listener that never cancels the event
     */
    default EventListener addListener(boolean alwaysCancelling, Consumer<T> listener) {
        return addListener(alwaysCancelling, Priority.NORMAL, listener);
    }

    /**
     * Adds an always cancelling listener to this EventBus with the specified priority.
     * @param alwaysCancelling If true, always cancel the event after calling the listener. This acts as if you
     *                         added a Predicate listener that always returns true, but with additional optimisations.
     *                         <p>If false, you should use {@link #addListener(byte, Consumer)} instead to avoid
     *                         unnecessary breaking changes if the event is no longer cancellable in the future</p>
     * @param listener The listener to add.
     * @return A reference that can be used to remove this listener later with {@link #removeListener(EventListener)}.
     * @see Priority For common priority values
     */
    EventListener addListener(boolean alwaysCancelling, byte priority, Consumer<T> listener);

    /**
     * Adds a possibly cancelling listener to this EventBus with the default priority of {@link Priority#NORMAL}.
     * @param listener The listener to add.
     * @return A reference that can be used to remove this listener later with {@link #removeListener(EventListener)}.
     */
    default EventListener addListener(Predicate<T> listener) {
        return addListener(Priority.NORMAL, listener);
    }

    /**
     * Adds a possibly cancelling listener to this EventBus with the specified priority.
     * @param priority The priority of this listener. Higher numbers are called first.
     * @param listener The listener to add.
     * @return A reference that can be used to remove this listener later with {@link #removeListener(EventListener)}.
     * @see Priority For common priority values
     */
    EventListener addListener(byte priority, Predicate<T> listener);

    EventListener addListener(ObjBooleanBiConsumer<T> listener);

    static <T extends Event<T> & EventCharacteristic.Cancellable> CancellableEventBus<T> create() {
        throw new UnsupportedOperationException("CancellableEventBus.create");
    }

    static <T extends Event<T> & EventCharacteristic.Cancellable> CancellableEventBus<T> create(Class<T> clazz) {
        throw new UnsupportedOperationException("CancellableEventBus.create");
    }
}
