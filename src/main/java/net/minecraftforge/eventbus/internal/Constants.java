package net.minecraftforge.eventbus.internal;

import net.minecraftforge.eventbus.api.event.Event;
import net.minecraftforge.eventbus.api.listener.EventListener;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Predicate;

final class Constants {
    private Constants() {}

    private static final Consumer<Event<?>> NO_OP_CONSUMER = event -> {};
    private static final Predicate<Event<?>> NO_OP_PREDICATE = event -> false;

    static final MethodHandle MH_NULL_CONSUMER = MethodHandles.constant(Consumer.class, null);
    static final MethodHandle MH_NO_OP_CONSUMER = MethodHandles.constant(Consumer.class, NO_OP_CONSUMER);

    static final MethodHandle MH_NULL_PREDICATE = MethodHandles.constant(Predicate.class, null);
    static final MethodHandle MH_NO_OP_PREDICATE = MethodHandles.constant(Predicate.class, NO_OP_PREDICATE);

    static final Comparator<EventListener> PRIORITY_COMPARATOR = (a, b) -> b.priority() - a.priority();

    static final int CHARACTERISTIC_SELF_DESTRUCTING = 1;
    static final int CHARACTERISTIC_MONITOR_AWARE = 2;
    static final int CHARACTERISTIC_SINGLE_THREADED = 4;

    @SuppressWarnings("unchecked")
    static <T> Consumer<T> getNoOpConsumer() {
        return (Consumer<T>) NO_OP_CONSUMER;
    }

    @SuppressWarnings("unchecked")
    static <T> Predicate<T> getNoOpPredicate() {
        return (Predicate<T>) NO_OP_PREDICATE;
    }
}
