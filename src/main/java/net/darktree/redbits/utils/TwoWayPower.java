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
        switch ( this ) {
            case FRONT: return Direction.AxisDirection.POSITIVE;
            case BACK: return Direction.AxisDirection.NEGATIVE;
        }

        return null;
    }

    public String asString() {
        return this.name;
    }
}
