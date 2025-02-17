/*
 * Copyright (c) Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.eventbus.testjar.events;

import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.EventCharacteristic;
import net.minecraftforge.eventbus.api.event.MutableEvent;

public class ResultEvent extends MutableEvent implements EventCharacteristic.SelfPosting<ResultEvent> {
    public static final EventBus<ResultEvent> BUS = EventBus.create(ResultEvent.class);

    private Result result = Result.DEFAULT;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    @Override
    public EventBus<ResultEvent> getDefaultBus() {
        return BUS;
    }
}
