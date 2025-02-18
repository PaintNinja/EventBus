package net.minecraftforge.eventbus.internal;

import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.*;
import net.minecraftforge.eventbus.api.listener.EventListener;

import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public record BusGroupImpl(
        String name,
        ConcurrentHashMap<Class<? extends Event>, EventBus<?>> eventBuses
) implements BusGroup {
    private static final Set<String> BUS_GROUP_NAMES = ConcurrentHashMap.newKeySet();

    public BusGroupImpl(String name) {
        this(name, new ConcurrentHashMap<>());
    }

    public BusGroupImpl {
        if (!BUS_GROUP_NAMES.add(Objects.requireNonNull(name)))
            throw new IllegalArgumentException("BusGroup name \"" + name + "\" is already in use");
    }

    @Override
    public void startup() {
        for (var eventBus : eventBuses.values())
            ((AbstractEventBusImpl<?, ?>) eventBus).startup();
    }

    @Override
    public void shutdown() {
        for (var eventBus : eventBuses.values())
            ((AbstractEventBusImpl<?, ?>) eventBus).shutdown();
    }

    @Override
    public void dispose() {
        for (var eventBus : eventBuses.values())
            ((AbstractEventBusImpl<?, ?>) eventBus).dispose();

        eventBuses.clear();
        BUS_GROUP_NAMES.remove(name);
    }

    @Override
    public Collection<EventListener> register(MethodHandles.Lookup callerLookup, Class<?> utilityClassWithStaticListeners) {
        return EventListenerFactory.register(this, callerLookup, utilityClassWithStaticListeners, null);
    }

    @Override
    public Collection<EventListener> register(MethodHandles.Lookup callerLookup, Object listener) {
        return EventListenerFactory.register(this, callerLookup, listener.getClass(), listener);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void unregister(Collection<EventListener> listeners) {
        if (listeners.isEmpty())
            throw new IllegalArgumentException("Listeners cannot be empty! You should be getting the collection from" +
                    "the BusGroup#register method.");

        for (var listener : listeners) {
            if (!name.equals(listener.busGroupName()))
                throw new IllegalArgumentException("Listener does not belong to this BusGroup!");

            getOrCreateEventBus((Class<? extends Event>) listener.eventType()).removeListener(listener);
        }
    }

    //region Internal access only
    @SuppressWarnings("unchecked")
    private <T extends Event> EventBus<T> createEventBus(Class<T> eventType) {
        if (InheritableEvent.class.isAssignableFrom(eventType)) {
            var maybeEventBus = eventBuses.get(eventType);
            if (maybeEventBus != null)
                return (EventBus<T>) maybeEventBus;
        } else if (eventBuses.containsKey(eventType)) {
            throw new IllegalArgumentException("EventBus for " + eventType + " already exists on BusGroup \"" + name + "\"");
        }

        List<EventBus<?>> parents = getParentEvents(eventType);

        var parentListeners = new ArrayList<EventListener>();
        for (var parent : parents) {
            parentListeners.addAll(((AbstractEventBusImpl<?, ?>) parent).backingList());
        }

//        return (EventBus<T>) eventBuses.computeIfAbsent(
//                eventType,
//                event -> {
                    @SuppressWarnings("rawtypes") // Raw types are unavoidable due to limitations in Java's type system.
                    var bus = EventCharacteristic.Cancellable.class.isAssignableFrom(eventType)
                            ? new CancellableEventBusImpl<>(this, (Class) (Class<? extends EventCharacteristic.Cancellable>) eventType, parentListeners)
                            : new EventBusImpl<>(this, eventType, parentListeners);

                    for (var parent : parents) {
                        ((AbstractEventBusImpl<?, ?>) parent).children().add(bus);
                    }

                    return bus;
//                }
//        );
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> EventBus<T> getOrCreateEventBus(Class<T> eventType) {
        return (EventBus<T>) eventBuses.computeIfAbsent(eventType, event -> createEventBus(eventType));
    }
    //endregion

    @Override
    public boolean equals(Object that) {
        return this == that || (that instanceof BusGroupImpl busGroup && name.equals(busGroup.name));
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    private <T extends Event> List<EventBus<?>> getParentEvents(Class<T> eventType) {
        var parentEvents = new ArrayList<EventBus<?>>();

        // first handle class inheritance (e.g. MyEvent extends ParentEvent)
        Class<? super T> parent = eventType.getSuperclass();
        if (parent != null // has a parent that's not Object
                && parent != MutableEvent.class // the parent isn't exactly MutableEvent
                && parent != InheritableEvent.class // the parent isn't exactly InheritableEvent
                && InheritableEvent.class.isAssignableFrom(parent) // implements InheritableEvent
                && !parent.isAnnotationPresent(MarkerEvent.class) // the parent hasn't opted out of listener inheritance
        ) {
            @SuppressWarnings("unchecked")
            var parentEvent = getOrCreateEventBus((Class<? extends Event>) parent);
            parentEvents.add(parentEvent);
        }

        // then handle interfaces (e.g. MyEvent implements MyEventInterface)
        for (var iface : eventType.getInterfaces()) {
            if (iface != InheritableEvent.class
                    && InheritableEvent.class.isAssignableFrom(iface)
                    && !iface.isAnnotationPresent(MarkerEvent.class)
            ) {
                @SuppressWarnings("unchecked")
                var parentEvent = getOrCreateEventBus((Class<? extends Event>) iface);
                parentEvents.add(parentEvent);
            }
        }

        return parentEvents;
    }
}
