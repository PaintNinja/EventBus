/*
 * Copyright (c) Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.eventbus.testjar;

import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.EventCharacteristic;
import net.minecraftforge.eventbus.api.event.InheritableEvent;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import net.minecraftforge.eventbus.testjar.events.Result;

public class DummyEvent extends MutableEvent implements InheritableEvent {
    public static final EventBus<DummyEvent> BUS = EventBus.create(DummyEvent.class);

    public static class GoodEvent extends DummyEvent {
        public static final EventBus<GoodEvent> BUS = EventBus.create(GoodEvent.class);
    }
    public static class BadEvent extends DummyEvent {}
    public static class CancellableEvent extends DummyEvent implements EventCharacteristic.Cancellable {}
    public static class ResultEvent extends DummyEvent {
        private Result result = Result.DEFAULT;

        public Result getResult() {
            return result;
        }

        public void setResult(Result result) {
            this.result = result;
        }
    }
}
