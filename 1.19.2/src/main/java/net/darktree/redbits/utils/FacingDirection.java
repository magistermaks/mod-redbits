package net.darktree.redbits.utils;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;

public enum FacingDirection implements StringIdentifiable {
	FRONT("front", Direction.AxisDirection.POSITIVE),
	BACK("back", Direction.AxisDirection.NEGATIVE);

	private final String name;
	private final Direction.AxisDirection direction;

	FacingDirection(String name, Direction.AxisDirection direction) {
		this.name = name;
		this.direction = direction;
	}

	public String toString() {
		return this.asString();
	}

	public Direction.AxisDirection asAxisDirection() {
		return direction;
	}

	public boolean asBoolean() {
		return this == FRONT;
	}

	public FacingDirection other() {
		return asBoolean() ? BACK : FRONT;
	}

	public static FacingDirection from(boolean front) {
		return front ? FRONT : BACK;
	}

	public static FacingDirection from(Direction.AxisDirection facing) {
		return from(facing == Direction.AxisDirection.POSITIVE);
	}

	public String asString() {
		return this.name;
	}
}
