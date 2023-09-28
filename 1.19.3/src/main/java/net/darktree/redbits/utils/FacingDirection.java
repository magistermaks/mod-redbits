package net.darktree.redbits.utils;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;

public enum FacingDirection implements StringIdentifiable {
	FRONT("front"),
	BACK("back");

	private final String name;

	FacingDirection(String name) {
		this.name = name;
	}

	public String toString() {
		return this.asString();
	}

	public Direction.AxisDirection asAxisDirection() {
		return asBoolean() ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE;
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
