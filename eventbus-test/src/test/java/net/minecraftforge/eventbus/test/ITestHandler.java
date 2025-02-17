/*
 * Copyright (c) Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.eventbus.test;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraftforge.eventbus.api.bus.BusGroup;

public interface ITestHandler {
    default void before(Consumer<Class<?>> validator, Supplier<BusGroup> builder) { before(); }
    default void before() {}
    default void after(Consumer<Class<?>> validator, Supplier<BusGroup> builder) { after(); }
    default void after() {}

    default void test(Consumer<Class<?>> validator, Supplier<BusGroup> busGroupSupplier) { test(); }
    default void test() {}
}
