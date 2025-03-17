/*
 * Copyright (c) Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

import org.jspecify.annotations.NullMarked;

/**
 * The EventBus module is split into two distinct packages: the {@linkplain net.minecraftforge.eventbus.api API} and the
 * {@linkplain net.minecraftforge.eventbus.internal internal implementation}. The API contains the interfaces for
 * consumers to interact with. The internal implementation is never exported and should not be accessed as it can be
 * changed at any time, without prior warning, and for any reason.
 *
 * @see net.minecraftforge.eventbus.api
 */
@NullMarked
module net.minecraftforge.eventbus {
    requires java.logging;
    requires org.jspecify;

    exports net.minecraftforge.eventbus.api.bus;
    exports net.minecraftforge.eventbus.api.event;
    exports net.minecraftforge.eventbus.api.event.characteristic;
    exports net.minecraftforge.eventbus.api.listener;

    exports net.minecraftforge.eventbus.internal to net.minecraftforge.eventbus.test;
}
