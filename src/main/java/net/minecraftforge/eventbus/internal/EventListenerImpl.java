/*
 * Copyright (c) Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.eventbus.internal;

import net.minecraftforge.eventbus.api.listener.EventListener;
import net.minecraftforge.eventbus.api.listener.ObjBooleanBiConsumer;
import net.minecraftforge.eventbus.api.listener.Priority;

import java.util.function.Consumer;
import java.util.function.Predicate;

public sealed interface EventListenerImpl extends EventListener {
    /**
     * @implNote This is a String instead of a BusGroup to avoid a circular hard reference between this and BusGroup.
     */
    @Override
    String busGroupName();

    sealed interface HasConsumer<T> extends EventListenerImpl {
        Consumer<T> consumer();
    }

    sealed interface HasPredicate<T> extends EventListenerImpl {
        Predicate<T> predicate();
    }

    record ConsumerListener(
            String busGroupName,
            Class<? extends Event> eventType,
            byte priority,
            Consumer<Event> consumer
    ) implements HasConsumer<Event> {}

    record PredicateListener(
            String busGroupName,
            Class<? extends Event> eventType,
            byte priority,
            Predicate<Event> predicate
    ) implements HasPredicate<Event> {
        public PredicateListener {
            assert priority != Priority.MONITOR : "Monitoring listeners cannot cancel events";
        }
    }

    record MonitoringListener(
            String busGroupName,
            Class<? extends Event> eventType,
            ObjBooleanBiConsumer<Event> booleanBiConsumer
    ) implements EventListenerImpl {
        public MonitoringListener(String busGroupName, Class<? extends Event> eventType, Consumer<Event> listener) {
            this(busGroupName, eventType, (event, wasCancelled) -> listener.accept(event));
        }

        @Override
        public byte priority() {
            return Priority.MONITOR;
        }
    }

    record WrappedConsumerListener(
            String busGroupName,
            Class<? extends Event> eventType,
            byte priority,
            boolean alwaysCancelling,
            Consumer<Event> consumer,
            Predicate<Event> predicate
    ) implements HasConsumer<Event>, HasPredicate<Event> {
        public WrappedConsumerListener(String busGroupName, Class<? extends Event> eventType, byte priority, Consumer<Event> consumer) {
            this(busGroupName, eventType, priority, false, consumer, wrap(false, consumer));
        }

        public WrappedConsumerListener(String busGroupName, Class<? extends Event> eventType, byte priority, boolean alwaysCancelling, Consumer<Event> consumer) {
            this(busGroupName, eventType, priority, alwaysCancelling, consumer, wrap(alwaysCancelling, consumer));
        }

        public WrappedConsumerListener {
            assert !alwaysCancelling && priority != Priority.MONITOR : "Monitoring listeners cannot cancel events";
        }

        /**
         * @implNote We avoid capturing the alwaysCancelling field in the lambda so that the bytecode is generated as a
         *           {@code ICONST_1} or {@code ICONST_0} instruction, rather than a field load.
         */
        private static Predicate<Event> wrap(boolean alwaysCancelling, Consumer<Event> consumer) {
            if (alwaysCancelling) {
                return event -> {
                    consumer.accept(event);
                    return true;
                };
            } else {
                return event -> {
                    consumer.accept(event);
                    return false;
                };
            }
        }

        // Don't factor in the wrapped predicate for equals() and hashCode()
        @Override
        public boolean equals(Object obj) {
            return obj instanceof WrappedConsumerListener that
                    && this.eventType == that.eventType
                    && this.priority == that.priority
                    && this.alwaysCancelling == that.alwaysCancelling
                    && this.busGroupName.equals(that.busGroupName)
                    && this.consumer.equals(that.consumer);
        }

        @Override
        public int hashCode() {
            return busGroupName.hashCode()
                    * 31 + eventType.hashCode()
                    * 31 + priority
                    * 31 + Boolean.hashCode(alwaysCancelling)
                    * 31 + consumer.hashCode();
        }
    }
}
