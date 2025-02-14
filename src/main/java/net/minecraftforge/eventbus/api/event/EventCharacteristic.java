package net.minecraftforge.eventbus.api.event;

import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.bus.EventBus;

/**
 * There are a number of optional characteristics that an event can have which can influence their behaviour
 * and the optimisation strategies that can be applied to them.
 */
public sealed interface EventCharacteristic {
    /**
     * A cancellable event returns {@code true} from {@link CancellableEventBus#post(Event)} if it was cancelled.
     * <p>When an event is cancelled, it will not be passed to any further non-monitor listeners.</p>
     */
    non-sealed interface Cancellable extends EventCharacteristic {}

    /**
     * A self-destructing event will dispose of its associated {@link EventBus} after it has been posted to free up
     * resources, after which it cannot be posted to again.
     * <p>This is useful for single-use lifecycle events.</p>
     */
    non-sealed interface SelfDestructing extends EventCharacteristic {}

    /**
     * Experimental feature - may be removed or renamed without notice.
     * <p>Events that are {@link MonitorAware} can provide stronger immutability guarantees to monitor listeners
     * by returning unmodifiable views or throwing exceptions on mutation attempts when monitoring.</p>
     * <p>Only supported for {@link MutableEvent} at this time.</p>
     */
    non-sealed interface MonitorAware extends EventCharacteristic {
        default boolean isMonitoring() {
            return ((MutableEvent<?>) this).isMonitoring;
        }
    }

    /**
     * Experimental feature - may be removed or renamed without notice.
     * <p>All {@link EventBus}es support concurrent operation by default, but you can opt out of this thread-safety
     * by using this characteristic if you know all event listeners are always registered and posted on the same
     * thread.</p>
     * <p>Warning: Incorrect usage of this characteristic may cause corruption, crashes and/or other unexpected
     * behaviour - avoid using if unsure.</p>
     */
    non-sealed interface SingleThreaded extends EventCharacteristic {}
}
