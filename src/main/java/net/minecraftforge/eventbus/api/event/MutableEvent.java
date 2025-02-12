package net.minecraftforge.eventbus.api.event;

@MarkerEvent
public non-sealed abstract class MutableEvent<T extends Event<T>> implements Event<T> {}
