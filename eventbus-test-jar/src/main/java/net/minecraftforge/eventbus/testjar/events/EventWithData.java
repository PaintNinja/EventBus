/*
 * Copyright (c) Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.eventbus.testjar.events;

import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.EventCharacteristic;
import net.minecraftforge.eventbus.api.event.RecordEvent;

public record EventWithData(String data, int foo, boolean bar) implements RecordEvent<EventWithData>, EventCharacteristic.SelfPosting<EventWithData> {
    public static final EventBus<EventWithData> BUS = EventBus.create(EventWithData.class);

    @Override
    public EventBus<EventWithData> getDefaultBus() {
        return BUS;
    }
}
