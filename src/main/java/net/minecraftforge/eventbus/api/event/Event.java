package net.minecraftforge.eventbus.api.event;

@MarkerEvent
public sealed interface Event permits InheritableEvent, MutableEvent, RecordEvent {}
