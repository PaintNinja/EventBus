package net.minecraftforge.eventbus.api.event;

@MarkerEvent
public non-sealed interface RecordEvent<T extends Record & Event<T>> extends Event<T> {}
