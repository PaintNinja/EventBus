/*
 * Copyright (c) Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.eventbus.api.bus;

import net.minecraftforge.eventbus.internal.Event;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;
import net.minecraftforge.eventbus.api.listener.EventListener;
import net.minecraftforge.eventbus.api.listener.Priority;
import net.minecraftforge.eventbus.internal.AbstractEventBusImpl;
import net.minecraftforge.eventbus.internal.BusGroupImpl;
import net.minecraftforge.eventbus.internal.EventBusImpl;

import java.util.function.Consumer;

/**
 * An EventBus is a simple event dispatching system that allows listeners to subscribe the related event and be notified
 * when it is {@linkplain #post(T) posted}. It can be created for a single type of event, after which listeners are
 * expected to {@linkplain #addListener(Consumer) register} themselves to it.
 *
 * <h2>Priority System</h2>
 * <p>When the event for an EventBus is posted, the listeners for the bus are gathered in order of their
 * {@linkplain EventListener#priority() priority}. The priority itself is stored as a byte (for performance reasons),
 * but there are a handful of standard priorities that can be found within the class, such as {@link Priority#NORMAL},
 * {@link Priority#HIGHEST}, etc.</p>
 * <p>Using a byte instead of an enum for the priority system gives the ability for listeners to order themselves
 * precisely.</p>
 *
 * <h2>Maintaining Listeners</h2>
 * <p>When a {@linkplain #addListener(Consumer) listener is added} to an EventBus, a reference to it is returned from
 * that same method call. This reference can be used to later remove the listener from the EventBus if it is no longer
 * needed. This can be useful for needing a listener for an event that is not
 * {@linkplain net.minecraftforge.eventbus.api.event.characteristic.SelfDestructing self-destructing} but needs to be
 * disposed of after a certain point.</p>
 *
 * {@snippet :
 * import net.minecraftforge.eventbus.api.event.RecordEvent;
 *
 * public record MyEvent() implements RecordEvent {
 *     public static final EventBus<MyEvent> BUS = EventBus.create(MyEvent.class);
 * }
 *
 * public static void registerListeners() {
 *     // Simple listener that prints a message
 *     var myListener = MyEvent.BUS.addListener(event -> System.out.println("Hello! I exist!"));
 *
 *     // Removing an added listener
 *     MyEvent.BUS.removeListener(myListener);
 *
 *     // Re-adding a removed listener
 *     MyEvent.BUS.addListener(myListener);
 * }
 * }
 *
 * @param <T> The type of event this EventBus is for
 * @see CancellableEventBus
 */
public sealed interface EventBus<T extends Event> permits CancellableEventBus, AbstractEventBusImpl, EventBusImpl {
    /**
     * Adds a listener to this EventBus with the default priority of {@link Priority#NORMAL}.
     * @param listener The listener to add
     * @return A reference that can be used to remove this listener later with {@link #removeListener(EventListener)}
     */
    EventListener addListener(Consumer<T> listener);

    /**
     * Adds a listener to this EventBus with the given priority.
     * @param priority The priority of this listener. Higher numbers are called first.
     * @param listener The listener to add
     * @return A reference that can be used to remove this listener later with {@link #removeListener(EventListener)}
     * @see Priority For common priority values
     */
    EventListener addListener(byte priority, Consumer<T> listener);

    /**
     * Re-adds a listener to this EventBus that was previously removed with {@link #removeListener(EventListener)}.
     * @param listener The exact same reference returned by an {@code addListener} method
     * @return The same reference that was passed in
     */
    EventListener addListener(EventListener listener);

    /**
     * Removes a listener from this EventBus that was previously added with one of the {@code addListener} methods.
     * @param listener The exact same reference returned by an {@code addListener} method
     */
    void removeListener(EventListener listener);

    /**
     * Fires the event for this EventBus to all listeners and returns the result of the cancellation.
     * @param event The instance of this event to post to listeners
     * @return {@code true} if the event implements {@link Cancellable} and the event was cancelled by a listener
     * @apiNote This method will <i>always return {@code false}</i> for EventBus instances that are not
     * {@linkplain CancellableEventBus cancellable}.
     * <p>If you need to create an event bus for a cancellable event, consider using {@link CancellableEventBus}.</p>
     * @see CancellableEventBus
     */
    boolean post(T event);

    /**
     * @param event The instance of this event to fire to listeners
     * @return The possibly mutated event instance after all applicable listeners have been called
     */
    T fire(T event);

    /**
     * If making a new event instance is expensive, you can check against this method to avoid creating a new instance
     * unnecessarily.
     * @apiNote You only need to check this if event creation is expensive. If it's cheap, just call {@link #post(Event)}
     *          or {@link #fire(Event)} directly and let the JIT handle it.
     * @return {@code true} if there are any listeners registered to this EventBus.
     */
    boolean hasListeners();

    /**
     * Creates a new EventBus for the given event type on the {@linkplain BusGroup#DEFAULT default} {@link BusGroup}.
     * <p><strong>Important:</strong> The returned EventBus <i>MUST</i> be stored in a {@code static final} field -
     * failing to do so will severely hurt performance.</p>
     * @apiNote There can only be one EventBus instance per event type per BusGroup.
     */
    @SuppressWarnings("ClassEscapesDefinedScope") // E can be a subtype of Event which is publicly accessible
    static <E extends Event> EventBus<E> create(Class<E> eventType) {
        return create(BusGroup.DEFAULT, eventType);
    }

    /**
     * Creates a new EventBus for the given event type on the given {@link BusGroup}.
     * <p><strong>Important:</strong> The returned EventBus <i>MUST</i> be stored in a {@code static final} field -
     * failing to do so will severely hurt performance.</p>
     * @apiNote There can only be one EventBus instance per event type per BusGroup.
     */
    @SuppressWarnings("ClassEscapesDefinedScope") // E can be a subtype of Event which is publicly accessible
    static <E extends Event> EventBus<E> create(BusGroup busGroup, Class<E> eventType) {
        return ((BusGroupImpl) busGroup).getOrCreateEventBus(eventType);
    }
}
