/*
 * Copyright (c) Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.eventbus.test.general;

import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.event.EventCharacteristic;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.eventbus.test.ITestHandler;

import java.lang.invoke.MethodHandles;

import static org.junit.jupiter.api.Assertions.*;

public class InterfaceEventHandler implements ITestHandler {
    private static boolean hit = false;

    public InterfaceEventHandler(boolean hasTransformer) {
    }

    @Override
    public void test() {
        assertDoesNotThrow(() -> BusGroup.DEFAULT.register(MethodHandles.lookup(), STATIC.class));
        testCall(true, "STATIC");
        assertDoesNotThrow(() -> BusGroup.DEFAULT.register(MethodHandles.lookup(), new INSTANCE() {}));
        testCall(true, "STATIC");
    }

    private void testCall(boolean expected, String name) {
        hit = false;
        new DummyEvent().post();
        assertEquals(expected, hit, name + " did not behave correctly");
    }

    public record DummyEvent() implements RecordEvent<DummyEvent>, EventCharacteristic.SelfPosting<DummyEvent> {
        public static final EventBus<DummyEvent> BUS = EventBus.create(DummyEvent.class);

        @Override
        public EventBus<DummyEvent> getDefaultBus() {
            return BUS;
        }
    }

    public interface STATIC {
        @SubscribeEvent
        static void handler(DummyEvent e) {
            hit = true;
        }
    }
    
    public interface INSTANCE {
        @SubscribeEvent
        default void handler(DummyEvent e) {
            hit = true;
        }
    }
}
