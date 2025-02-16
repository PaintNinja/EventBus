package net.minecraftforge.eventbus.testjar.events;

public enum Result {
    DENY,
    DEFAULT,
    ALLOW;

    public boolean isDefault() {
        return this == DEFAULT;
    }

    public boolean isDenied() {
        return this == DENY;
    }

    public boolean isAllowed() {
        return this == ALLOW;
    }
}
