/*
 * Copyright (c) Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.eventbus.api.event.characteristic;

import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.internal.Event;

/**
 * A cancellable event returns {@code true} from {@link CancellableEventBus#post(Event)} if it was cancelled. The
 * following key points must be known regarding cancellable events:
 * <ul>
 *     <li>When an event is cancelled, it will not be passed to any further non-{@linkplain net.minecraftforge.eventbus.api.listener.Priority#MONITOR monitor} listeners.</li>
 *     <li>Cancellable events <strong>must be used with a {@linkplain CancellableEventBus cancellable event bus}</strong> in order to function as intended.</li>
 * </ul>
 *
 * @see CancellableEventBus
 */
public non-sealed interface Cancellable extends EventCharacteristic {}
