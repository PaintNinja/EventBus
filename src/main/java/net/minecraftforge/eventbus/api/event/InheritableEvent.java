package net.minecraftforge.eventbus.api.event;

@MarkerEvent
public non-sealed interface InheritableEvent<T extends Event<T>> extends Event<T> {}
