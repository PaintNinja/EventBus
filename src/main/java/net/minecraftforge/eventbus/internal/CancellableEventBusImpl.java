package net.minecraftforge.eventbus.internal;

import net.minecraftforge.eventbus.api.event.Event;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.event.EventCharacteristic;
import net.minecraftforge.eventbus.api.listener.ListenerMetadata;
import net.minecraftforge.eventbus.api.listener.ObjBooleanBiConsumer;

import java.util.function.Consumer;
import java.util.function.Predicate;

public final class CancellableEventBusImpl<T extends Event<T> & EventCharacteristic.Cancellable>
        extends EventBusImpl<T> implements CancellableEventBus<T> {
    @Override
    public ListenerMetadata addListener(boolean alwaysCancelling, byte priority, Consumer<T> listener) {
        throw new UnsupportedOperationException("CancellableEventBusImpl.addListener");
    }

    @Override
    public ListenerMetadata addListener(byte priority, Predicate<T> listener) {
        throw new UnsupportedOperationException("CancellableEventBusImpl.addListener");
    }

    @Override
    public ListenerMetadata addListener(ObjBooleanBiConsumer<T> listener) {
        throw new UnsupportedOperationException("CancellableEventBusImpl.addListener");
    }
}
