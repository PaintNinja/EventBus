/*
 * Copyright (c) Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.eventbus.api.event;

import net.minecraftforge.eventbus.internal.Event;

/**
 * This top-level event implementation exists as a bare interface to allow events to implement it however they wish.
 * Unlike {@link MutableEvent}, which is an object that must be extended from, InheritableEvent can be applied to any
 * type that needs to have information sent to listeners via an
 * {@linkplain net.minecraftforge.eventbus.api.bus.EventBus event bus}.
 *
 * @see MutableEvent
 * @see RecordEvent
 */
public non-sealed interface InheritableEvent extends Event {}
