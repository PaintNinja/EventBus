package net.minecraftforge.eventbus.api.listener;

import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.internal.ListenerDataImpl;

/**
 * Users can retain instances of this interface to remove listeners that were previously added to the same
 * {@link EventBus}.
 * <p>Internally, this acts as a wrapper over lambdas to give them identity, enrich debug info and to allow
 * various conversion operations to different lambda types.</p>
 */
public sealed interface EventListener permits ListenerDataImpl {
    default byte priority() {
        return Priority.NORMAL;
    }
}
