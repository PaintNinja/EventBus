package net.minecraftforge.eventbus.internal;

import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.event.Event;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.listener.EventListener;
import net.minecraftforge.eventbus.api.listener.Priority;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.VolatileCallSite;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static net.minecraftforge.eventbus.internal.Constants.*;

public record EventBusImpl<T extends Event<T>>(
        String busGroupName,
        Class<T> eventType,
        CallSite invokerCallSite,
        List<EventListener> backingList,
        Set<EventListener> monitorBackingSet,
        List<AbstractEventBusImpl<?, ?>> children,
        AtomicBoolean alreadyInvalidated,
        AtomicBoolean shutdownFlag,
        int eventCharacteristics
) implements EventBus<T>, AbstractEventBusImpl<T, Consumer<T>> {
    public EventBusImpl(BusGroup busGroup, Class<T> eventType, List<EventListener> backingList) {
        this(busGroup.name(), eventType, backingList, AbstractEventBusImpl.computeEventCharacteristics(eventType));
    }

    public EventBusImpl(String busGroupName, Class<T> eventType, List<EventListener> backingList, int eventCharacteristics) {
        this(
                busGroupName,
                eventType,
                (eventCharacteristics & CHARACTERISTIC_SINGLE_THREADED) != 0
                        ? new MutableCallSite(backingList.isEmpty() ? MH_NO_OP_CONSUMER : MH_NULL_CONSUMER)
                        : new VolatileCallSite(backingList.isEmpty() ? MH_NO_OP_CONSUMER : MH_NULL_CONSUMER),
                backingList,
                new HashSet<>(),
                new ArrayList<>(),
                new AtomicBoolean(),
                new AtomicBoolean(),
                eventCharacteristics
        );
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"}) // T extends Event, so this is safe.
    public EventListener addListener(Consumer<T> listener) {
        return addListener(new EventListenerImpl.ConsumerListener(busGroupName, eventType, Priority.NORMAL, (Consumer<Event>) (Consumer) listener));
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"}) // T extends Event, so this is safe.
    public EventListener addListener(byte priority, Consumer<T> listener) {
        return addListener(
                priority == Priority.MONITOR
                        ? new EventListenerImpl.MonitoringListener(busGroupName, eventType, (Consumer<Event>) (Consumer) listener)
                        : new EventListenerImpl.ConsumerListener(busGroupName, eventType, priority, (Consumer<Event>) (Consumer) listener)
        );
    }

    @Override
    public boolean post(T event) {
        getInvoker().accept(event);
        return false;
    }

    @Override
    public T fire(T event) {
        getInvoker().accept(event);
        return event;
    }

    @Override
    public boolean hasListeners() {
        return getInvoker() != MH_NO_OP_CONSUMER;
    }

    //region Invoker
    @Override // overrides from AbstractEventBusImpl
    @SuppressWarnings("unchecked")
    public Consumer<T> maybeGetInvoker() {
        try {
            return (Consumer<T>) invokerCallSite.getTarget().invokeExact();
        } catch (Throwable t) {
            throw new RuntimeException(t); // should never happen, but we should throw if it somehow does
        }
    }

    @Override // overrides from AbstractEventBusImpl
    public void invalidateInvoker() {
        invokerCallSite.setTarget(backingList.isEmpty() ? MH_NO_OP_CONSUMER : MH_NULL_CONSUMER);
    }

    @Override // overrides from AbstractEventBusImpl
    public Consumer<T> buildInvoker() {
        synchronized (backingList) {
            backingList.sort(PRIORITY_COMPARATOR);

            Consumer<T> invoker = InvokerFactory.createMonitoringInvoker(
                    eventType, eventCharacteristics, backingList, monitorBackingSet
            );

            if ((eventCharacteristics & CHARACTERISTIC_SELF_DESTRUCTING) != 0)
                invoker = invoker.andThen(event -> dispose());

            setInvoker(invoker);
            alreadyInvalidated.set(false);
            return invoker;
        }
    }

    @Override // overrides from AbstractEventBusImpl
    public void setNoOpInvoker() {
        invokerCallSite.setTarget(MH_NO_OP_CONSUMER);
    }

    /**
     * Should only be called from inside a {@code synchronized(backingList)} block.
     */
    private Consumer<T> setInvoker(Consumer<T> invoker) {
        invokerCallSite.setTarget(MethodHandles.constant(Consumer.class, invoker));
        return invoker;
    }
    //endregion
}
