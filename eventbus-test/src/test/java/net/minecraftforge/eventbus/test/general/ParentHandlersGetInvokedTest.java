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
        var listener = SuperEvent.BUS.addListener(event -> {
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

        superEventHandled.set(false);
        SuperEvent.BUS.removeListener(listener);
        SuperEvent.BUS.addListener(listener);
        SubEvent.BUS.post(new SubEvent());

        assertTrue(subEventHandled.get(), "Handler was not invoked for SuperEvent after re-adding listener");
    }

    private static sealed class SuperEvent extends MutableEvent implements InheritableEvent permits SubEvent {
        private static final EventBus<SuperEvent> BUS = EventBus.create(SuperEvent.class);
    }

    private static final class SubEvent extends SuperEvent {
        private static final EventBus<SubEvent> BUS = EventBus.create(SubEvent.class);
    }
}
