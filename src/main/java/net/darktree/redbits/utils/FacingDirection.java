package net.darktree.redbits.utils;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

public enum FacingDirection implements StringRepresentable {
	FRONT("front", Direction.AxisDirection.POSITIVE),
	BACK("back", Direction.AxisDirection.NEGATIVE);

	private final String name;
	private final Direction.AxisDirection direction;

	FacingDirection(String name, Direction.AxisDirection direction) {
		this.name = name;
		this.direction = direction;
	}

	public String toString() {
		return this.getSerializedName();
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

	public String getSerializedName() {
		return this.name;
	}
}
