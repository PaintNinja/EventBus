package net.minecraftforge.eventbus.api.event;

import net.minecraftforge.eventbus.internal.MutableEventInternals;

@MarkerEvent
public non-sealed abstract class MutableEvent<T extends Event<T>> extends MutableEventInternals<T> implements Event<T> {}
