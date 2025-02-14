package net.minecraftforge.eventbus.api.listener;

import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.Event;
import net.minecraftforge.eventbus.internal.EventListenerImpl;

import java.util.function.Consumer;

/**
 * Users can retain instances of this interface to remove listeners that were previously added to the same
 * {@link EventBus}.You can obtain instances of this interface by calling any of the {@code addListener} methods
 * on an EventBus, such as {@link EventBus#addListener(Consumer)}.
 * <p>Internally, this acts as a wrapper over lambdas to give them identity, enrich debug info and to allow
 * various conversion operations to different lambda types.</p>
 */
public sealed interface EventListener permits EventListenerImpl {
    String busGroupName();

    Class<? extends Event<?>> eventType();

    /**
     * @see Priority
     */
    byte priority();

    /**
     * @see CancellableEventBus#addListener(boolean, Consumer)
     */
    default boolean alwaysCancelling() {
        return false;
    }
}
