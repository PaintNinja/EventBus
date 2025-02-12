package net.minecraftforge.eventbus.internal;

import net.minecraftforge.eventbus.api.bus.BusGroup;

public final class BusGroupImpl implements BusGroup {
    @Override
    public void startup() {
        throw new UnsupportedOperationException("BusGroupImpl.startup");
    }

    @Override
    public void shutdown() {
        throw new UnsupportedOperationException("BusGroupImpl.shutdown");
    }

    @Override
    public void dispose() {
        throw new UnsupportedOperationException("BusGroupImpl.dispose");
    }
}
