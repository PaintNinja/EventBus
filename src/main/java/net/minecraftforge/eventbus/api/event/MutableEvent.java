/*
 * Copyright (c) Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.eventbus.api.event;

import net.minecraftforge.eventbus.internal.Event;
import net.minecraftforge.eventbus.internal.MutableEventInternals;

/**
 * This top-level event implementation exists as a base object that can be expanded upon to contain information to be
 * sent to listeners via an {@linkplain net.minecraftforge.eventbus.api.bus.EventBus event bus}.
 * <p>The primary difference between MutableEvent and {@link InheritableEvent} is that MutableEvent is
 * {@linkplain net.minecraftforge.eventbus.api.event.characteristic.MonitorAware monitor-aware}. Although it is the
 * MonitorAware interface that enforces this contract, its default implementation works natively with MutableEvent
 * without needing any additional implementations from the consumer.</p>
 *
 * @see net.minecraftforge.eventbus.api.event.characteristic.MonitorAware MonitorAware
 * @see InheritableEvent
 * @see RecordEvent
 */
public non-sealed abstract class MutableEvent extends MutableEventInternals implements Event {}
