package net.minecraftforge.eventbus.internal;

import net.minecraftforge.eventbus.api.event.MarkerEvent;
import net.minecraftforge.eventbus.api.event.MutableEvent;

@MarkerEvent
public sealed abstract class MutableEventInternals permits MutableEvent {
    public transient boolean isMonitoring;
}
