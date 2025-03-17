/*
 * Copyright (c) Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.eventbus.api.event.characteristic;

import net.minecraftforge.eventbus.api.event.MutableEvent;
import net.minecraftforge.eventbus.internal.MutableEventInternals;

/**
 * Events that are {@link MonitorAware} can provide stronger immutability guarantees to
 * {@linkplain net.minecraftforge.eventbus.api.listener.Priority#MONITOR monitor} listeners by returning unmodifiable
 * views or throwing exceptions on mutation attempts when monitoring.
 * <p>Only supported for {@link MutableEvent} at this time.</p>
 *
 * @apiNote <strong>This feature is experimental - it may be removed, renamed or otherwise changed without
 * notice.</strong>
 * @see MutableEvent
 */
public non-sealed interface MonitorAware extends EventCharacteristic {
    default boolean isMonitoring() {
        assert this instanceof MutableEvent; // note: MutableEvent extends MutableEventInternals
        return ((MutableEventInternals) this).isMonitoring;
    }
}
