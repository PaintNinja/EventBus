package net.minecraftforge.eventbus.api.event;

import net.minecraftforge.eventbus.api.bus.EventBus;

@MarkerEvent
public sealed interface Event<T extends Event<T>> permits InheritableEvent, MutableEvent, RecordEvent {
    EventBus<T> defaultBus();

    /**
     * Convenience method for posting this event instance to the default bus.
     */
    @SuppressWarnings("unchecked")
    default boolean post() {
        return defaultBus().post((T) this);
    }

    /**
     * Convenience method for firing this event instance to the default bus.
     */
    @SuppressWarnings("unchecked")
    default T fire() {
        return defaultBus().fire((T) this);
    }
}
