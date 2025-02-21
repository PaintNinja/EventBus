/*
 * Copyright (c) Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.eventbus.test.general;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.eventbus.api.event.characteristic.SelfPosting;
import net.minecraftforge.eventbus.test.ITestHandler;

public class EventFiringEventTest implements ITestHandler {
    @Override
    public void test() {
        AtomicBoolean handled1 = new AtomicBoolean(false);
        AtomicBoolean handled2 = new AtomicBoolean(false);

        Event1.BUS.addListener(event1 -> {
            new Event2().post();
            handled1.set(true);
        });
        Event2.BUS.addListener(event2 -> handled2.set(true));

        new Event1().post();

        assertTrue(handled1.get(), "handled Event1");
        assertTrue(handled2.get(), "handled Event2");
    }

    public record Event1() implements RecordEvent, SelfPosting<Event1> {
        public static final EventBus<Event1> BUS = EventBus.create(Event1.class);

        @Override
        public EventBus<Event1> getDefaultBus() {
            return BUS;
        }
    }

    public record Event2() implements RecordEvent, SelfPosting<Event2> {
        public static final EventBus<Event2> BUS = EventBus.create(Event2.class);

        @Override
        public EventBus<Event2> getDefaultBus() {
            return BUS;
        }
    }
}
