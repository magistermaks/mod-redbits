package net.darktree.redbits.utils;

import net.darktree.redbits.blocks.AbstractRedstoneGate;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public enum TwoWayPower implements StringIdentifiable {
	FRONT("front", Direction.AxisDirection.POSITIVE),
	BACK("back", Direction.AxisDirection.NEGATIVE),
	NONE("none", null);

	private final String name;
	private final Direction.AxisDirection direction;

	TwoWayPower(String name, Direction.AxisDirection direction) {
		this.name = name;
		this.direction = direction;
	}

	public String toString() {
		return this.asString();
	}

	public boolean isAligned(Direction facing) {
		return direction != null && direction == facing.getDirection();
	}

	public Direction asDirection(Direction.Axis axis) {
		return Direction.from(axis, direction);
	}

	public String asString() {
		return this.name;
	}

	public static Unit getPower(World world, BlockPos pos, AbstractRedstoneGate gate, TwoWayPower power, Direction.Axis axis) {

		if (power == TwoWayPower.NONE) {
			Unit a = getPower(world, pos, gate, TwoWayPower.FRONT, axis);
			if (a.getPower() > 0) return a;

			Unit b = getPower(world, pos, gate, TwoWayPower.BACK, axis);
			if (b.getPower() > 0) return b;

			return new Unit(TwoWayPower.NONE, 0);
		}

		Direction direction = power.asDirection(axis);
		BlockPos source = pos.offset(direction);

		return new Unit(power, gate.getInputPower(world, source, direction));
	}

	public static class Unit {

		private final int power;
		private final TwoWayPower direction;

		public Unit(TwoWayPower direction, int power) {
			this.direction = direction;
			this.power = power;
		}

		public int getPower() {
			return power;
		}

		public boolean hasPower() {
			return power != 0;
		}

		public TwoWayPower getDirection() {
			return direction;
		}

	}


}
