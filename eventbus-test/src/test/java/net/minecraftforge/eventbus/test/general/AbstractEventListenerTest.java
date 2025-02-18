/*
 * Copyright (c) Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.eventbus.test.general;

import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.InheritableEvent;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import net.minecraftforge.eventbus.test.ITestHandler;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AbstractEventListenerTest implements ITestHandler {
    public void test(Consumer<Class<?>> validator, Supplier<BusGroup> busGroupSupplier) {
        validator.accept(AbstractSuperEvent.class);
        validator.accept(AbstractSubEvent.class);

        BusGroup busGroup = busGroupSupplier.get();
        AtomicBoolean abstractSuperEventHandled = new AtomicBoolean(false);
        AtomicBoolean concreteSuperEventHandled = new AtomicBoolean(false);
        AtomicBoolean abstractSubEventHandled = new AtomicBoolean(false);
        AtomicBoolean concreteSubEventHandled = new AtomicBoolean(false);

        var abstractSuperEventBus = EventBus.create(busGroup, AbstractSuperEvent.class);
        var concreteSuperEventBus = EventBus.create(busGroup, ConcreteSuperEvent.class);
        var abstractSubEventBus = EventBus.create(busGroup, AbstractSubEvent.class);
        var concreteSubEventBus = EventBus.create(busGroup, ConcreteSubEvent.class);

        abstractSuperEventBus.addListener(event -> abstractSuperEventHandled.set(true));
        concreteSuperEventBus.addListener(event -> concreteSuperEventHandled.set(true));
        abstractSubEventBus.addListener(event -> abstractSubEventHandled.set(true));
        concreteSubEventBus.addListener(event -> concreteSubEventHandled.set(true));

        concreteSubEventBus.post(new ConcreteSubEvent());

        assertTrue(abstractSuperEventHandled.get(), "handled abstract super event");
        assertTrue(concreteSuperEventHandled.get(), "handled concrete super event");
        assertTrue(abstractSubEventHandled.get(), "handled abstract sub event");
        assertTrue(concreteSubEventHandled.get(), "handled concrete sub event");
    }

    public interface AbstractSuperEvent extends InheritableEvent {}

    public static class ConcreteSuperEvent extends MutableEvent implements AbstractSuperEvent {}

    public static class AbstractSubEvent extends ConcreteSuperEvent {}

    public static final class ConcreteSubEvent extends AbstractSubEvent {}
}
