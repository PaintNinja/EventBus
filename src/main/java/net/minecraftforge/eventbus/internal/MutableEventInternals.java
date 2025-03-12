/*
 * Copyright (c) Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.eventbus.internal;

import net.minecraftforge.eventbus.api.event.MutableEvent;

public sealed abstract class MutableEventInternals permits MutableEvent {
    public transient boolean isMonitoring;
}
