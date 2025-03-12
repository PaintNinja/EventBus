/*
 * Copyright (c) Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */
open module net.minecraftforge.eventbus.jmh {
    requires net.minecraftforge.eventbus;
    requires org.jspecify;
    requires jmh.core;

    requires net.minecraftforge.eventbus.testjars;
    requires net.minecraftforge.unsafe;
}
