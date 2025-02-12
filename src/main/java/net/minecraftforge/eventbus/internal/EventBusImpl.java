package net.minecraftforge.eventbus.internal;

import net.minecraftforge.eventbus.api.event.Event;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.listener.ListenerMetadata;

import java.util.function.Consumer;

public sealed class EventBusImpl<T extends Event<T>> implements EventBus<T> permits CancellableEventBusImpl {
    @Override
    public ListenerMetadata addListener(Consumer<T> listener) {
        throw new UnsupportedOperationException("EventBusImpl.addListener");
    }

    @Override
    public ListenerMetadata addListener(byte priority, Consumer<T> listener) {
        throw new UnsupportedOperationException("EventBusImpl.addListener");
    }

    @Override
    public void removeListener(ListenerMetadata listener) {
        throw new UnsupportedOperationException("EventBusImpl.removeListener");
    }

    @Override
    public boolean post(T event) {
        throw new UnsupportedOperationException("EventBusImpl.post");
    }

    @Override
    public T fire(T event) {
        throw new UnsupportedOperationException("EventBusImpl.fire");
    }

    @Override
    public boolean hasListeners() {
        throw new UnsupportedOperationException("EventBusImpl.hasListeners");
    }
}
