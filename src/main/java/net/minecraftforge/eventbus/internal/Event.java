package net.minecraftforge.eventbus.internal;

import net.minecraftforge.eventbus.api.event.InheritableEvent;
import net.minecraftforge.eventbus.api.event.MarkerEvent;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import net.minecraftforge.eventbus.api.event.RecordEvent;

@MarkerEvent
public sealed interface Event permits InheritableEvent, MutableEvent, RecordEvent {}
