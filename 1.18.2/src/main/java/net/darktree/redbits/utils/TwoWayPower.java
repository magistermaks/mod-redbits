package net.darktree.redbits.utils;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;

public enum TwoWayPower implements StringIdentifiable {
    FRONT("front"),
    BACK("back"),
    NONE("none");

    private final String name;

    TwoWayPower(String name) {
        this.name = name;
    }

    public String toString() {
        return this.asString();
    }

    public boolean isAligned( Direction.AxisDirection axis ) {
        return asAxisDirection() == axis && axis != null;
    }

    public Direction.AxisDirection asAxisDirection() {
        return switch (this) {
            case FRONT -> Direction.AxisDirection.POSITIVE;
            case BACK -> Direction.AxisDirection.NEGATIVE;
            default -> null;
        };
    }

    public String asString() {
        return this.name;
    }
}
