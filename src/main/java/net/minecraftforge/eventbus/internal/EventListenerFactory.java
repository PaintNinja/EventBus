/*
 * Copyright (c) Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.eventbus.internal;

import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;
import net.minecraftforge.eventbus.api.listener.EventListener;
import net.minecraftforge.eventbus.api.listener.ObjBooleanBiConsumer;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import org.jspecify.annotations.Nullable;

import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

final class EventListenerFactory {
    private EventListenerFactory() {}

    private static final MethodType RETURNS_CONSUMER = MethodType.methodType(Consumer.class);
    private static final MethodType RETURNS_PREDICATE = MethodType.methodType(Predicate.class);
    private static final MethodType RETURNS_MONITOR = MethodType.methodType(ObjBooleanBiConsumer.class);

    /** The method type of the {@link Consumer} functional interface ({@code void accept(Object)}) */
    private static final MethodType CONSUMER_FI_TYPE = MethodType.methodType(void.class, Object.class);

    /** The method type of the {@link Predicate} functional interface ({@code boolean test(Object)}) */
    private static final MethodType PREDICATE_FI_TYPE = CONSUMER_FI_TYPE.changeReturnType(boolean.class);

    /** The method type of the {@link ObjBooleanBiConsumer} functional interface ({@code void accept(Object, boolean)}) */
    private static final MethodType MONITOR_FI_TYPE = MethodType.methodType(void.class, Object.class, boolean.class);

    private static final Map<Method, MethodHandle> LMF_CACHE = new ConcurrentHashMap<>();

    // Todo: [EB][Bulk registration] make the error messages more descriptive
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Collection<EventListener> register(BusGroupImpl busGroup, MethodHandles.Lookup callerLookup,
                                                     Class<?> listenerClass, @Nullable Object listenerInstance) {
        int count = 0;
        Class<?> firstValidListenerEventType = null;

        Method[] declaredMethods = listenerClass.getDeclaredMethods();
        if (declaredMethods.length == 0)
            throw new IllegalArgumentException("No declared methods found in " + listenerClass);

        var listeners = new ArrayList<EventListener>();
        for (var method : declaredMethods) {
            if (listenerInstance == null && !Modifier.isStatic(method.getModifiers()))
                continue;

            int paramCount = method.getParameterCount();
            if (paramCount == 0 || paramCount > 2)
                continue;

            if (!method.isAnnotationPresent(SubscribeEvent.class))
                continue;

            Class<?>[] parameterTypes = method.getParameterTypes();
            if (!Event.class.isAssignableFrom(parameterTypes[0]))
                throw new IllegalArgumentException();

            // determine the listener type from its parameters and return type
            Class<?> returnType = method.getReturnType();
            Class<? extends Event> eventType = (Class<? extends Event>) parameterTypes[0];
            var subscribeEventAnnotation = method.getAnnotation(SubscribeEvent.class);

            if (paramCount == 1) {
                if (returnType == void.class) {
                    if (Cancellable.class.isAssignableFrom(eventType)) {
                        // Consumer<Event & EventCharacteristic.Cancellable>
                        var eventBus = ((CancellableEventBus) busGroup.getOrCreateEventBus(eventType));
                        if (subscribeEventAnnotation.alwaysCancelling()) {
                            listeners.add(eventBus.addListener(
                                    subscribeEventAnnotation.priority(), true, createConsumer(callerLookup, method, listenerInstance)));
                        } else {
                            eventBus.addListener(subscribeEventAnnotation.priority(), createConsumer(callerLookup, method, listenerInstance));
                        }
                    } else {
                        // Consumer<Event>
                        listeners.add(busGroup.getOrCreateEventBus(eventType)
                                .addListener(subscribeEventAnnotation.priority(), createConsumer(callerLookup, method, listenerInstance)));
                    }
                } else if (returnType == boolean.class) {
                    // Predicate<Event & EventCharacteristic.Cancellable>
                    if (!Cancellable.class.isAssignableFrom(eventType))
                        throw new IllegalArgumentException();

                    if (subscribeEventAnnotation.alwaysCancelling())
                        throw new IllegalArgumentException("Always cancelling listeners must return void");

                    listeners.add(((CancellableEventBus) busGroup.getOrCreateEventBus(eventType))
                            .addListener(subscribeEventAnnotation.priority(), createPredicate(callerLookup, method, listenerInstance)));
                } else {
                    throw new IllegalArgumentException();
                }
            } else {
                // ObjBooleanBiConsumer<Event>
                if (returnType != void.class)
                    throw new IllegalArgumentException();

                if (subscribeEventAnnotation.alwaysCancelling())
                    throw new IllegalArgumentException("Monitoring listeners cannot cancel events");

                listeners.add(((CancellableEventBus) busGroup.getOrCreateEventBus(eventType))
                        .addListener(createMonitor(callerLookup, method, listenerInstance)));
            }

            if (firstValidListenerEventType == null)
                firstValidListenerEventType = eventType;

            count++;
        }

        if (count == 0)
            throw new IllegalArgumentException("No listeners found in " + listenerClass);
        else if (firstValidListenerEventType == null) {
            throw new IllegalArgumentException("No valid listeners found in " + listenerClass);
        } else if (count == 1) {
            throw new IllegalArgumentException("Only a single listener found in " + listenerClass + ". You should directly call addListener() on the EventBus of " + firstValidListenerEventType.getSimpleName() + " instead.");
        }

        return listeners;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Event> Consumer<T> createConsumer(MethodHandles.Lookup callerLookup, Method callback,
                                                                @Nullable Object instance) {
        boolean isStatic = Modifier.isStatic(callback.getModifiers());
        var factoryMH = getOrMakeFactory(callerLookup, callback, isStatic, instance, RETURNS_CONSUMER, CONSUMER_FI_TYPE, "accept");

        try {
            return isStatic
                    ? (Consumer<T>) factoryMH.invokeExact()
                    : (Consumer<T>) factoryMH.invokeExact(instance);
        } catch (Exception e) {
            throw makeRuntimeException(callback, e);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Event> Predicate<T> createPredicate(MethodHandles.Lookup callerLookup, Method callback,
                                                                  @Nullable Object instance) {
        boolean isStatic = Modifier.isStatic(callback.getModifiers());
        var factoryMH = getOrMakeFactory(callerLookup, callback, isStatic, instance, RETURNS_PREDICATE, PREDICATE_FI_TYPE, "test");

        try {
            return isStatic
                    ? (Predicate<T>) factoryMH.invokeExact()
                    : (Predicate<T>) factoryMH.invokeExact(instance);
        } catch (Exception e) {
            throw makeRuntimeException(callback, e);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Event> ObjBooleanBiConsumer<T> createMonitor(MethodHandles.Lookup callerLookup,
                                                                           Method callback, @Nullable Object instance) {
        boolean isStatic = Modifier.isStatic(callback.getModifiers());
        var factoryMH = getOrMakeFactory(callerLookup, callback, isStatic, instance, RETURNS_MONITOR, MONITOR_FI_TYPE, "accept");

        try {
            return isStatic
                    ? (ObjBooleanBiConsumer<T>) factoryMH.invokeExact()
                    : (ObjBooleanBiConsumer<T>) factoryMH.invokeExact(instance);
        } catch (Exception e) {
            throw makeRuntimeException(callback, e);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private static MethodHandle getOrMakeFactory(MethodHandles.Lookup callerLookup, Method callback, boolean isStatic,
                                                 @Nullable Object instance, MethodType factoryReturnType,
                                                 MethodType fiMethodType, String fiMethodName) {
        if (Constants.ALLOW_DUPE_LISTENERS)
            return makeFactory(callerLookup, callback, isStatic, instance, factoryReturnType, fiMethodType, fiMethodName);
        else
            return LMF_CACHE.computeIfAbsent(callback, callbackMethod ->
                    makeFactory(callerLookup, callbackMethod, isStatic, instance, factoryReturnType, fiMethodType, fiMethodName));
    }

    private static MethodHandle makeFactory(MethodHandles.Lookup callerLookup, Method callback, boolean isStatic,
                                            @Nullable Object instance, MethodType factoryReturnType,
                                            MethodType fiMethodType, String fiMethodName) {
        try {
            var mh = callerLookup.unreflect(callback);

            MethodType factoryType = isStatic
                    ? factoryReturnType
                    : factoryReturnType.insertParameterTypes(0, Objects.requireNonNull(instance).getClass());

            MethodHandle lmf = LambdaMetafactory.metafactory(
                    callerLookup, fiMethodName, factoryType, fiMethodType, mh,
                    isStatic ? mh.type() : mh.type().dropParameterTypes(0, 1)
            ).getTarget();

            if (isStatic)
                return lmf;

            // wrap the target MH in an Object -> instance class cast to allow for invokeExact()
            return lmf.asType(factoryType.changeParameterType(0, Object.class));
        } catch (Exception e) {
            throw makeRuntimeException(callback, e);
        }
    }

    private static RuntimeException makeRuntimeException(Method callback, Exception e) {
        return switch (e) {
            case IllegalAccessException iae -> {
                var errMsg = "Failed to create listener";
                if (!Modifier.isPublic(callback.getModifiers()))
                    errMsg += " - is it public?";

                yield new RuntimeException(errMsg, iae);
            }
            case NullPointerException npe -> new RuntimeException(
                    "Failed to create listener - was given a non-static method without an instance to invoke it with",
                    npe
            );
            default -> new RuntimeException("Failed to create listener", e);
        };
    }
}
