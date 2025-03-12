/*
 * Copyright (c) Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.eventbus.api.event;

import net.minecraftforge.eventbus.internal.Event;

/**
 * This top-level implementation is for records to directly implement. This is enforced at runtime, as any non-record
 * event that implements this interface will cause an {@link IllegalArgumentException} to be thrown when an
 * {@linkplain net.minecraftforge.eventbus.api.bus.EventBus event bus} is created for it.
 *
 * @apiNote Although records can inherit {@link InheritableEvent} without implementing RecordEvent, it is highly
 * recommended to implement this instead as it will boost performance drastically. EventBus is designed to take
 * advantage of the finalized state of records.
 * @see InheritableEvent
 * @see MutableEvent
 */
public non-sealed interface RecordEvent extends Event {}
