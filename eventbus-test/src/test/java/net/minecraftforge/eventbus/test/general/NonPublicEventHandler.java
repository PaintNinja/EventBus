/*
 * Copyright (c) Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.eventbus.test.general;

import java.lang.invoke.MethodHandles;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.test.ITestHandler;

import static org.junit.jupiter.api.Assertions.*;

public class NonPublicEventHandler implements ITestHandler {
    private final boolean hasTransformer;
    private static boolean hit = false;

    public NonPublicEventHandler(boolean hasTransformer) {
        this.hasTransformer = hasTransformer;
    }

    @Override
    public void test(Consumer<Class<?>> validator, Supplier<BusGroup> busGroupSupplier) {
        var bus = busGroupSupplier.get();
        assertDoesNotThrow(() -> bus.register(MethodHandles.lookup(), new PUBLIC()));
        testCall(bus, true, "PUBLIC");

//        if (hasTransformer) {
            assertDoesNotThrow(() -> bus.register(MethodHandles.lookup(), new PROTECTED()));
            testCall(bus, true, "PROTECTED");
            assertDoesNotThrow(() -> bus.register(MethodHandles.lookup(), new DEFAULT()));
            testCall(bus, true, "DEFAULT");
            assertDoesNotThrow(() -> bus.register(MethodHandles.lookup(), new PRIVATE()));
            testCall(bus, true, "PRIVATE");
//        } else {
//            assertThrows(IllegalArgumentException.class, () -> bus.register(new PROTECTED()));
//            assertThrows(IllegalArgumentException.class, () -> bus.register(new DEFAULT()));
//            //assertThrows(IllegalArgumentException.class, () -> bus.register(new PRIVATE()));
//        }
    }

    private void testCall(BusGroup busGroup, boolean expected, String name) {
        hit = false;
        EventBus.create(busGroup, TestEvent.class).post(new TestEvent());
        assertEquals(expected, hit, name + " did not behave correctly");
    }

    public record TestEvent() implements RecordEvent<TestEvent> {}

    public static class PUBLIC {
        @SubscribeEvent
        public void handler(TestEvent e) {
            hit = true;
        }
    }
    public static class PRIVATE {
        @SubscribeEvent
        private void handler(TestEvent e) {
            hit = true;
        }
    }
    public static class PROTECTED {
        @SubscribeEvent
        protected void handler(TestEvent e) {
            hit = true;
        }
    }
    public static class DEFAULT {
        @SubscribeEvent
        void handler(TestEvent e) {
            hit = true;
        }
    }
}
