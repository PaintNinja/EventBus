/*
 * Copyright (c) Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.eventbus.api.listener;

import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Consumer;

/**
 * This annotation is used to mark methods as event handlers. The contract for the method that has this annotation
 * applied is the same as if it was treated as a lambda and fed into {@link EventBus#addListener(Consumer)} or one of
 * its sister methods from {@link CancellableEventBus} that accept a {@linkplain java.util.function.Predicate predicate}
 * or {@linkplain ObjBooleanBiConsumer object-boolean bi-consumer}.
 * <p>Classes that contain methods annotated with SubscribeEvent must be registered with the applicable
 * {@link BusGroup}.</p>
 * {@snippet :
 * import net.minecraftforge.eventbus.api.event.InheritableEvent;
 * import java.lang.invoke.MethodHandles;
 *
 * // in MyEvent.java
 * public class MyEvent implements InheritableEvent {
 *     public static final EventBus<MyEvent> BUS = EventBus.create(Main.GROUP, MyEvent.class);
 * }
 *
 * // in MyClass.java
 * public class MyClass {
 *     @SubscribeEvent
 *     public static void onEvent(MyEvent event) {
 *         System.out.println("Hello! I exist!");
 *     }
 *
 *     @SubscribeEvent(priority = Priority.LOW)
 *     public static void onEventLater(MyEvent event) {
 *         System.out.println("I also exist! But am called slightly later!");
 *     }
 *
 *     public static void init() {
 *         Main.GROUP.register(MethodHandles.lookup(), MyClass.class);
 *     }
 * }
 *
 * // in Main.java
 * public class Main {
 *     public static final BusGroup GROUP = BusGroup.create("my_group");
 *
 *     public static void main(String[] args) {
 *         MyClass.init();
 *     }
 * }
 *}
 *
 * <p>The same can also be done for cancellable events. As mentioned earlier, shaping the targeted method in the form
 * of a predicate or object-boolean bi-consumer will yield the same results when registered with a BusGroup</p>
 * {@snippet :
 * import net.minecraftforge.eventbus.api.event.MutableEvent;
 * import net.minecraftforge.eventbus.api.event.characteristic.MonitorAware;
 * import java.lang.invoke.MethodHandles;
 *
 * // in MyEvent.java
 * public class MyCancellableEvent extends MutableEvent implements MonitorAware {
 *     public static final CancellableEventBus<MyEvent> BUS = CancellableEventBus.create(Main.GROUP, MyCancellableEvent.class);
 *
 *     private boolean somethingImportant = true;
 *
 *     public void setSomethingImportant(boolean value) {
 *         if (!this.isMonitoring())
 *             this.somethingImportant = value;
 *     }
 * }
 *
 * // in MyClass.java
 * public class MyClass {
 *     @SubscribeEvent
 *     public static boolean onEvent(MyCancellableEvent event) {
 *         System.out.println("Hello! I exist!");
 *         System.out.println("Hello! I will not cancel this event!");
 *         return false;
 *     }
 *
 *     @SubscribeEvent(priority = Priority.LOW, alwaysCancelling = true)
 *     public static void onEventLater(MyCancellableEvent event) {
 *         System.out.println("I also exist! But I will cancel this event!");
 *     }
 *
 *     @SubscribeEvent(priority = Priority.LOWEST)
 *     public static void onEventLatest(MyCancellableEvent event) {
 *         System.out.println("I don't exist! I can't run because the event's been cancelled!");
 *     }
 *
 *     @SubscribeEvent // because this is an object-boolean bi-consumer, priority is implicitly MONITOR
 *     public static void onEventMonitoring(MyCancellableEvent event, boolean cancelled) {
 *         System.out.println("I exist! I'm a monitoring listener and will always run!");
 *         // this won't do anything:
 *         event.setSomethingImportant(false);
 *     }
 *
 *     public static void init() {
 *         Main.GROUP.register(MethodHandles.lookup(), MyClass.class);
 *     }
 * }
 *
 * // in Main.java
 * public class Main {
 *     public static final BusGroup GROUP = BusGroup.create("my_group");
 *
 *     public static void main(String[] args) {
 *         MyClass.init();
 *     }
 * }
 *}
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SubscribeEvent {
    /**
     * @return The priority of the listener.
     * @see Priority
     */
    byte priority() default Priority.NORMAL;

    /**
     * If the event is cancellable, setting this to true will make the listener always cancel the event.
     *
     * @implSpec If true, the annotated method must return {@code void} and the event must implement
     * {@link Cancellable}.
     */
    boolean alwaysCancelling() default false;
}
