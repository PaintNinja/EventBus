/*
 * Copyright (c) Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.eventbus.internal;

import net.minecraftforge.eventbus.api.event.InheritableEvent;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import net.minecraftforge.eventbus.api.event.RecordEvent;

/**
 * The base-level Event interface, which all events implement. This interface is sealed to only allow the top-level
 * implementations provided by the EventBus API to be inherited from. This is an intentional design choice and allows
 * for significant optimizations within the internal implementation.
 */
public sealed interface Event permits InheritableEvent, MutableEvent, RecordEvent {}
