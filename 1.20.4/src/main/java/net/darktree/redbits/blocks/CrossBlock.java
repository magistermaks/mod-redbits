package net.darktree.redbits.blocks;

import net.darktree.redbits.utils.TwoWayPower;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.tick.TickPriority;

@SuppressWarnings("deprecation")
public class CrossBlock extends BridgeBlock {

	private static final Direction[] TARGETS = new Direction[] {Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP};

	public CrossBlock(Settings settings) {
		super(settings);
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		int power = super.getWeakRedstonePower(state, world, pos, direction);

		if (power == 0 && direction == Direction.DOWN && state.get(X_POWER).any() && state.get(Z_POWER).any()) {
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
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		TwoWayPower x = state.get(X_POWER);
		TwoWayPower z = state.get(Z_POWER);

		if (x != TwoWayPower.NONE || z != TwoWayPower.NONE) {
			super.randomDisplayTick(state, world, pos, random);
		}

		if (x != TwoWayPower.NONE && z != TwoWayPower.NONE) {
			double px = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
			double py = pos.getY() + 0.7 + (random.nextDouble() - 0.5) * 0.2;
			double pz = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.2;

			world.addParticle(DustParticleEffect.DEFAULT, px, py, pz, 0.0, 0.0, 0.0);
		}

	}

}
