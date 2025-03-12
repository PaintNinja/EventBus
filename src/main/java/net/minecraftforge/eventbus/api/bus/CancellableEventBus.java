/*
 * Copyright (c) Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.eventbus.api.bus;

import net.minecraftforge.eventbus.internal.Event;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;
import net.minecraftforge.eventbus.api.listener.EventListener;
import net.minecraftforge.eventbus.api.listener.ObjBooleanBiConsumer;
import net.minecraftforge.eventbus.api.listener.Priority;
import net.minecraftforge.eventbus.internal.BusGroupImpl;
import net.minecraftforge.eventbus.internal.CancellableEventBusImpl;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A cancellable EventBus behaves the same as a {@linkplain EventBus standard EventBus}, but with the added ability to
 * handle {@linkplain Cancellable cancellable} events.
 *
 * <h2>Cancellation</h2>
 * <p>Cancellable events are given special treatment by the EventBus system, as they have the unique ability to stop the
 * execution of the posting of an event before the remaining listeners are able to interact with it. Additionally, when
 * an event is cancelled, the only listeners that will receive it from that point on are
 * {@linkplain Priority#MONITOR monitoring} listeners.</p>
 * <p>This class has several additional methods that aim to make adding listeners for cancellable events as easy as
 * possible. {@linkplain #addListener(Consumer) Consumers} can be used to listen to a cancellable event if it has no
 * need to cancel, or if it is a listener that will {@linkplain #addListener(boolean, Consumer) always cancel}.
 * {@linkplain #addListener(Predicate) Predicates} can be used to listen and potentially cancel an event by returning
 * {@code true} when it is notified. {@linkplain #addListener(ObjBooleanBiConsumer) Object-boolean bi-consumers} can be
 * used for monitoring listeners to receive events even if they have been cancelled, where the object is the event and
 * the boolean is the cancelled state.</p>
 *
 * {@snippet :
 * import net.minecraftforge.eventbus.api.event.RecordEvent;
 *
 * public record MyCancellableEvent() implements Cancellable, RecordEvent {
 *     public static final CancellableEventBus<MyCancellableEvent> BUS = CancellableEventBus.create(MyCancellableEvent.class);
 * }
 *
 * public static void registerListeners() {
 *     // Consumer that will always cancel the event
 *     MyCancellableEvent.BUS.addListener(true, event -> {
 *         System.out.println("Hello! I exist!");
 *         System.out.println("Time to cancel!");
 *     });
 *
 *     // Predicate that might cancel the event
 *     MyCancellableEvent.BUS.addListener(event -> Math.random() > 0.5);
 *
 *     MyCancellableEvent.BUS.addListener((event, cancelled) -> {
 *         System.out.println("I'm a monitoring listener!");
 *         System.out.printf("Was %s cancelled? %s", event.getClass().getSimpleName(), cancelled);
 *     });
 * }
 * }
 *
 * @param <T>
 * @see EventBus
 * @see Cancellable
 */
public sealed interface CancellableEventBus<T extends Event & Cancellable>
        extends EventBus<T> permits CancellableEventBusImpl {
    /**
     * Adds an always cancelling listener to this EventBus with the default priority of {@link Priority#NORMAL}.
     * @param alwaysCancelling If true, always cancel the event after calling the listener. This acts as if you
     *                         added a Predicate listener that always returns true, but with additional optimisations.
     *                         <p>If false, you should use {@link #addListener(Consumer)} instead to avoid unnecessary
     *                         breaking changes if the event is no longer cancellable in the future.</p>
     * @param listener The listener to add.
     * @return A reference that can be used to remove this listener later with {@link #removeListener(EventListener)}.
     * @see #addListener(Predicate)
     * @see #addListener(Consumer)
     */
    default EventListener addListener(boolean alwaysCancelling, Consumer<T> listener) {
        return addListener(Priority.NORMAL, alwaysCancelling, listener);
    }

    /**
     * Adds an always cancelling listener to this EventBus with the specified priority.
     * @param alwaysCancelling If true, always cancel the event after calling the listener. This acts as if you
     *                         added a Predicate listener that always returns true, but with additional optimisations.
     *                         <p>If false, you should use {@link #addListener(byte, Consumer)} instead to avoid
     *                         unnecessary breaking changes if the event is no longer cancellable in the future</p>
     * @param listener The listener to add.
     * @return A reference that can be used to remove this listener later with {@link #removeListener(EventListener)}.
     * @see Priority
     */
    EventListener addListener(byte priority, boolean alwaysCancelling, Consumer<T> listener);

    /**
     * Adds a possibly cancelling listener to this EventBus with the default priority of {@link Priority#NORMAL}.
     * @param listener The listener to add.
     * @return A reference that can be used to remove this listener later with {@link #removeListener(EventListener)}.
     */
    EventListener addListener(Predicate<T> listener);

    /**
     * Adds a possibly cancelling listener to this EventBus with the specified priority.
     * @param priority The priority of this listener. Higher numbers are called first.
     * @param listener The listener to add.
     * @return A reference that can be used to remove this listener later with {@link #removeListener(EventListener)}.
     * @see Priority
     */
    EventListener addListener(byte priority, Predicate<T> listener);

    /**
     * Adds a monitoring listener to this EventBus with an unchanging priority of {@link Priority#MONITOR}.
     * @param listener The listener to add.
     * @return A reference that can be used to remove this listener later with {@link #removeListener(EventListener)}.
     * @see Priority#MONITOR
     */
    EventListener addListener(ObjBooleanBiConsumer<T> listener);

    /**
     * Creates a new CancellableEventBus for the given event type on the {@linkplain BusGroup#DEFAULT default} {@link BusGroup}.
     * @param eventType The type of event the bus will have
     * @param <T> The type of event
     * @return The new CancallableEventBus
     */
    @SuppressWarnings("ClassEscapesDefinedScope") // E can be a subtype of Event which is publicly accessible
    static <T extends Event & Cancellable> CancellableEventBus<T> create(Class<T> eventType) {
        return create(BusGroup.DEFAULT, eventType);
    }

    /**
     * Creates a new CancellableEventBus for the given event type on the given BusGroup.
     * @param eventType The type of event the bus will have
     * @param <T> The type of event
     * @return The new CancallableEventBus
     */
    @SuppressWarnings("ClassEscapesDefinedScope") // E can be a subtype of Event which is publicly accessible
    static <T extends Event & Cancellable> CancellableEventBus<T> create(BusGroup busGroup, Class<T> eventType) {
        return (CancellableEventBus<T>) ((BusGroupImpl) busGroup).getOrCreateEventBus(eventType);
    }
}
