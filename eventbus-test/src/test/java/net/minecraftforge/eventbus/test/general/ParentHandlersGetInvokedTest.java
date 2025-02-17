/*
 * Copyright (c) Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.eventbus.test.general;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.*;
import net.minecraftforge.eventbus.test.ITestHandler;

public class ParentHandlersGetInvokedTest implements ITestHandler {
    @Override
    public void test() {
        AtomicBoolean superEventHandled = new AtomicBoolean(false);
        AtomicBoolean subEventHandled = new AtomicBoolean(false);
        SuperEvent.BUS.addListener(event -> {
            Class<? extends SuperEvent> eventClass = event.getClass();
            if (eventClass == SuperEvent.class) {
                superEventHandled.set(true);
            } else if (eventClass == SubEvent.class) {
                subEventHandled.set(true);
            }
        });

        SuperEvent.BUS.post(new SuperEvent());
        SubEvent.BUS.post(new SubEvent());

        assertTrue(superEventHandled.get(), "Handler was not invoked for SuperEvent");
        assertTrue(subEventHandled.get(), "Handler was not invoked for SubEvent");
    }

    public static class SuperEvent extends MutableEvent {
        public static final EventBus<SuperEvent> BUS = EventBus.create(SuperEvent.class);
    }

    public static final class SubEvent extends SuperEvent {
        public static final EventBus<SubEvent> BUS = EventBus.create(SubEvent.class);
    }
}
