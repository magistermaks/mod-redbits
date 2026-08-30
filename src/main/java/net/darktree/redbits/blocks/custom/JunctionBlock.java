package net.darktree.redbits.blocks.custom;

import net.darktree.redbits.utils.TwoWayPower;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("deprecation")
public class JunctionBlock extends BridgeBlock {

	private static final Direction[] TARGETS = new Direction[] {Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP};

	public JunctionBlock(Properties settings) {
		super(settings);
	}

	@Override
	public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {
		int power = super.getSignal(state, world, pos, direction);

		if (power == 0 && direction == Direction.DOWN && state.getValue(X_POWER).any() && state.getValue(Z_POWER).any()) {
			return 15;
		}

		return power;
	}

	@Override
	protected Direction[] getTargetDirections() {
		return TARGETS;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
		TwoWayPower x = state.getValue(X_POWER);
		TwoWayPower z = state.getValue(Z_POWER);

		if (x != TwoWayPower.NONE || z != TwoWayPower.NONE) {
			super.animateTick(state, world, pos, random);
		}

		if (x != TwoWayPower.NONE && z != TwoWayPower.NONE) {
			double px = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
			double py = pos.getY() + 0.7 + (random.nextDouble() - 0.5) * 0.2;
			double pz = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.2;

			world.addParticle(DustParticleOptions.REDSTONE, px, py, pz, 0.0, 0.0, 0.0);
		}

	}

}
