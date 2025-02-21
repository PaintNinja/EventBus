/*
 * Copyright (c) Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.eventbus.testjar.events;

import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.event.EventCharacteristic;
import net.minecraftforge.eventbus.api.event.RecordEvent;

public record CancelableEvent() implements RecordEvent, EventCharacteristic.Cancellable, EventCharacteristic.SelfPosting<CancelableEvent> {
    public static final CancellableEventBus<CancelableEvent> BUS = CancellableEventBus.create(CancelableEvent.class);

    @Override
    public CancellableEventBus<CancelableEvent> getDefaultBus() {
        return BUS;
    }
}
