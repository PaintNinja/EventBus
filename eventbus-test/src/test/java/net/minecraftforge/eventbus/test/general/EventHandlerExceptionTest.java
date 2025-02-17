/*
 * Copyright (c) Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.eventbus.test.general;

import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.test.ITestHandler;
import net.minecraftforge.eventbus.testjar.DummyEvent;
import net.minecraftforge.eventbus.testjar.EventBusTestClass;
import static org.junit.jupiter.api.Assertions.*;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EventHandlerExceptionTest implements ITestHandler {
    @Override
    public void test(Consumer<Class<?>> validator, Supplier<BusGroup> busGroupSupplier) {
        validator.accept(DummyEvent.class);
        validator.accept(DummyEvent.BadEvent.class);

        var busGroup = busGroupSupplier.get();
        var listener = new EventBusTestClass();
        listener.register(busGroup);
        assertThrows(RuntimeException.class, () -> {
            EventBus.create(DummyEvent.BadEvent.class).post(new DummyEvent.BadEvent());
        });
    }
}
