/*
 * Copyright (c) Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.eventbus.api.bus;

import net.minecraftforge.eventbus.internal.Event;
import net.minecraftforge.eventbus.api.listener.EventListener;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.eventbus.internal.BusGroupImpl;

import java.lang.invoke.MethodHandles;
import java.util.Collection;

/**
 * A collection of {@link EventBus} instances that are grouped together for easier management.
 */
public sealed interface BusGroup permits BusGroupImpl {
    /**
     * The default BusGroup provided by EventBus. If when creating an {@link EventBus} the BusGroup is not specified, it
     * will be registered in this one.
     *
     * @see #create(String)
     */
    BusGroup DEFAULT = create("default");

    /**
     * Creates a new BusGroup with the given name using the default {@link Event} base type.
     *
     * @param name The unique name to use
     * @return The new BusGroup
     * @throws IllegalArgumentException If a BusGroup with the given name already exists
     */
    static BusGroup create(String name) {
        return new BusGroupImpl(name, Event.class);
    }

    /**
     * Creates a new BusGroup with the given name and the given base type.
     *
     * @param name The unique name to use
     * @return The new BusGroup
     * @throws IllegalArgumentException If a BusGroup with the given name already exists
     * @apiNote In theory, it is possible to use any base type when creating a BusGroup. However, it is recommended to
     * either use a direct subtype of {@link Event} or use {@link #create(String)} which uses the default type.
     * @see #create(String)
     */
    static BusGroup create(String name, Class<?> baseType) {
        return new BusGroupImpl(name, baseType);
    }

    /**
     * The unique name of this BusGroup.
     */
    String name();

    /**
     * Starts up all EventBus instances associated with this BusGroup, allowing events to be posted again after a
     * previous call to {@link #shutdown()}.
     */
    void startup();

    /**
     * Shuts down all EventBus instances associated with this BusGroup, preventing any further events from being posted
     * until {@link #startup()} is called.
     */
    void shutdown();

    /**
     * Shuts down all EventBus instances associated with this BusGroup, unregisters all listeners and frees resources
     * no longer needed.
     * <p>Warning: This is a destructive operation - this BusGroup should not be used again after calling this method.
     * If you need to re-use this BusGroup, use {@link #shutdown()} instead.</p>
     */
    void dispose();

    /**
     * Experimental feature - may be removed, renamed or otherwise changed without notice.
     * <p>Trims the backing lists of all EventBus instances associated with this BusGroup to free up resources.</p>
     * <p>Warning: This is only intended to be called <b>once</b> after all listeners are registered - calling this
     * repeatedly may hurt performance.</p>
     */
    void trim();

    /**
     * Registers all static methods annotated with {@link SubscribeEvent} in the given class.
     *
     * @param callerLookup {@link MethodHandles#lookup()} from the class containing listeners
     * @param utilityClassWithStaticListeners the class containing the static listeners
     * @return A collection of the registered listeners, which can be used to optionally unregister them later
     *
     * @apiNote This method only registers static listeners.
     *          <p>If you want to register both instance and static methods, use
     *          {@link BusGroup#register(MethodHandles.Lookup, Object)} instead.</p>
     */
    Collection<EventListener> register(MethodHandles.Lookup callerLookup, Class<?> utilityClassWithStaticListeners);

    /**
     * Registers all methods annotated with {@link SubscribeEvent} in the given object.
     *
     * @param callerLookup {@link MethodHandles#lookup()} from the class containing the listeners
     * @param listener the object containing the static and/or instance listeners
     * @return A collection of the registered listeners, which can be used to optionally unregister them later
     *
     * @apiNote If you know all the listeners are static methods, use
     *          {@link BusGroup#register(MethodHandles.Lookup, Class)} instead for better registration performance.
     */
    Collection<EventListener> register(MethodHandles.Lookup callerLookup, Object listener);

    /**
     * Unregisters the given listeners from this BusGroup.
     * @param listeners A collection of listeners to unregister, obtained from
     *                  {@link #register(MethodHandles.Lookup, Class)} or {@link #register(MethodHandles.Lookup, Object)}
     */
    void unregister(Collection<EventListener> listeners);
}
