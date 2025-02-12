/*
 * Copyright (c) Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */
open module net.minecraftforge.eventbus {
    requires org.apache.logging.log4j;

    exports net.minecraftforge.eventbus.api.bus;
    exports net.minecraftforge.eventbus.api.event;
    exports net.minecraftforge.eventbus.api.listener;
}
