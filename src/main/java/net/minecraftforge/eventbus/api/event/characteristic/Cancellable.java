package net.minecraftforge.eventbus.api.event.characteristic;

import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.event.Event;

/**
 * A cancellable event returns {@code true} from {@link CancellableEventBus#post(Event)} if it was cancelled.
 * <p>When an event is cancelled, it will not be passed to any further non-monitor listeners.</p>
 */
public non-sealed interface Cancellable extends EventCharacteristic {}
