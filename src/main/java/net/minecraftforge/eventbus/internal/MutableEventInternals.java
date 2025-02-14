package net.minecraftforge.eventbus.internal;

import net.minecraftforge.eventbus.api.event.Event;
import net.minecraftforge.eventbus.api.event.MarkerEvent;
import net.minecraftforge.eventbus.api.event.MutableEvent;

@MarkerEvent
public sealed abstract class MutableEventInternals<T extends Event<T>> permits MutableEvent {
    public transient boolean isMonitoring;
}
