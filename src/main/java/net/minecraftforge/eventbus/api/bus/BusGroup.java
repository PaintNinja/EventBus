package net.minecraftforge.eventbus.api.bus;

import net.minecraftforge.eventbus.internal.BusGroupImpl;

/**
 * A collection of {@link EventBus} instances that are grouped together for easier management.
 */
public sealed interface BusGroup permits BusGroupImpl {
    BusGroup DEFAULT = create("default");

    static BusGroup create(String name) {
        throw new UnsupportedOperationException("BusGroup.create");
    }

    /**
     * Starts up all EventBus instances associated with this BusGroup, allowing events to be posted again after a
     * previous call to {@link #shutdown()}.
     */
    void startup();

    /**
     * Shuts down all EventBus instances associated with this BusGroup, preventing any further events from being posted
     * until {@link #startup()} is called.
     */
    void shutdown();

    /**
     * Shuts down all EventBus instances associated with this BusGroup, unregisters all listeners and frees resources
     * no longer needed.
     * <p>Warning: This is a destructive operation - this BusGroup should not be used again after calling this method.</p>
     */
    void dispose();
}
