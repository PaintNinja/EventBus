/*
 * Copyright (c) Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.eventbus.test;

import net.minecraftforge.eventbus.api.bus.BusGroup;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class TestNoLoaderBase {
    private static final AtomicInteger count = new AtomicInteger();

    private void validate(Class<?> clazz) {
        // We expect transformers to not run, so make sure LISTENER_LIST does not exist
        assertFalse(Whitebox.hasField(clazz, "LISTENER_LIST"), "EventSubclassTransformer ran on " + clazz.getName() + ", we wanted to use non-transformed events");
    }

    private BusGroup builder() {
        return BusGroup.create("test" + count.incrementAndGet());
    }

    protected void doTest(ITestHandler handler) {
        handler.before(this::validate, this::builder);
        handler.test(this::validate, this::builder);
        handler.after(this::validate, this::builder);
    }
}
