package net.minecraftforge.eventbus.internal;

import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.Event;
import net.minecraftforge.eventbus.api.event.EventCharacteristic;
import net.minecraftforge.eventbus.api.listener.EventListener;
import net.minecraftforge.eventbus.api.listener.Priority;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public sealed interface AbstractEventBusImpl<T extends Event, I> extends EventBus<T>
        permits CancellableEventBusImpl, EventBusImpl {
    //region Record component accessors
    List<EventListener> backingList();
    Set<EventListener> monitorBackingSet();
    List<AbstractEventBusImpl<?, ?>> children();
    AtomicBoolean shutdownFlag();
    AtomicBoolean alreadyInvalidated();
    int eventCharacteristics();
    //endregion

    static int computeEventCharacteristics(Class<?> eventType) {
        int characteristics = 0;

        if (EventCharacteristic.SelfDestructing.class.isAssignableFrom(eventType))
            characteristics |= Constants.CHARACTERISTIC_SELF_DESTRUCTING;

        if (EventCharacteristic.MonitorAware.class.isAssignableFrom(eventType))
            characteristics |= Constants.CHARACTERISTIC_MONITOR_AWARE;

        if (EventCharacteristic.SingleThreaded.class.isAssignableFrom(eventType))
            characteristics |= Constants.CHARACTERISTIC_SINGLE_THREADED;

        return characteristics;
    }

    static List<AbstractEventBusImpl<?, ?>> makeEventChildrenList(Class<?> eventType) {
        if (eventType.isSealed())
            return new ArrayList<>(eventType.getPermittedSubclasses().length);

        if (Modifier.isFinal(eventType.getModifiers()))
            return Collections.emptyList();

        return new ArrayList<>();
    }

    @Override
    default EventListener addListener(EventListener listener) {
        if ((eventCharacteristics() & Constants.CHARACTERISTIC_SINGLE_THREADED) != 0) {
            synchronized (backingList()) {
                return addListenerInternal(listener);
            }
        } else {
            return addListenerInternal(listener);
        }
    }

    @Override
    default void removeListener(EventListener listener) {
        if ((eventCharacteristics() & Constants.CHARACTERISTIC_SINGLE_THREADED) != 0) {
            synchronized (backingList()) {
                removeListenerInternal(listener);
            }
        } else {
            removeListenerInternal(listener);
        }
    }

    private EventListener addListenerInternal(EventListener listener) {
        boolean added = listener.priority() == Priority.MONITOR
                ? monitorBackingSet().add(listener)
                : backingList().add(listener);

        if (added) {
            invalidateInvoker();
            for (var child : children()) {
                child.addListener(listener);
            }
        }
        return listener;
    }

    private void removeListenerInternal(EventListener listener) {
        boolean removed = listener.priority() == Priority.MONITOR
                ? monitorBackingSet().remove(listener)
                : backingList().remove(listener);

        if (removed) {
            invalidateInvoker();
            for (var child : children()) {
                child.removeListener(listener);
            }
        }
    }

    //region Invoker
    /**
     * @return The invoker if it is still valid, otherwise null.
     */
    I maybeGetInvoker();

    /**
     * Should only be called from inside a {@code synchronized(backingList)} block.
     */
    void invalidateInvoker();

    /**
     * Should only be called when the invoker returned by {@link #maybeGetInvoker()} is null, indicating that it has
     * been invalidated and needs to be rebuilt.
     */
    I buildInvoker();

    void setNoOpInvoker();

    /**
     * @return The invoker, creating it if necessary. Never returns null.
     */
    default I getInvoker() {
        var invoker = maybeGetInvoker();
        if (invoker == null)
            invoker = buildInvoker();

        return invoker;
    }
    //endregion

    default void startup() {
        if (!shutdownFlag().compareAndSet(true, false))
            return;

        synchronized (backingList()) {
            // Force invalidate the invoker to remove the no-op invoker that might've been set by shutdown()
            alreadyInvalidated().set(false);
            invalidateInvoker();
        }
        children().forEach(AbstractEventBusImpl::startup);
    }

    default void shutdown() {
        if (!shutdownFlag().compareAndSet(false, true))
            return;

        synchronized (backingList()) {
            // When shutdown, set the invoker to a no-op invoker and prevent it from being invalidated
            // on calls to addListener() to keep the no-op invoker
            setNoOpInvoker();
            alreadyInvalidated().set(true);
        }
        children().forEach(AbstractEventBusImpl::shutdown);
    }

    default void dispose() {
        shutdown();
        synchronized (backingList()) {
            backingList().clear();
            monitorBackingSet().clear();

            if (backingList() instanceof ArrayList<?> arrayList)
                arrayList.trimToSize();
        }
        children().forEach(AbstractEventBusImpl::dispose);
    }
}
