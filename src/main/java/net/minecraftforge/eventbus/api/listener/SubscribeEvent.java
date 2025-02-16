package net.minecraftforge.eventbus.api.listener;

import net.minecraftforge.eventbus.api.event.EventCharacteristic;

import java.lang.annotation.*;

@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SubscribeEvent {
    byte priority() default Priority.NORMAL;

    /**
     * If the event is cancellable, setting this to true will make the listener always cancel the event.
     *
     * @apiNote If true, the annotated method must return {@code void} and the event must implement
     *          {@link EventCharacteristic.Cancellable}.
     */
    boolean alwaysCancelling() default false;
}
