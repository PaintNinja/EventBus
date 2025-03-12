/*
 * Copyright (c) Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.eventbus.api.listener;

import java.util.function.BiConsumer;

/**
 * A {@link BiConsumer} that takes an object and a primitive boolean, to avoid boxing. Used for
 * {@linkplain net.minecraftforge.eventbus.api.listener.Priority#MONITOR monitor} listeners.
 */
@FunctionalInterface
public interface ObjBooleanBiConsumer<T> {
    /**
     * @param obj The object of type {@link T}, usually an event.
     * @param bool The boolean value, usually an event's cancelled status.
     * @see BiConsumer#accept(Object, Object)
     */
    void accept(T obj, boolean bool);
}
