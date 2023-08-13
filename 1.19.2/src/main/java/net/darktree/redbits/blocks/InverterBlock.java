package net.darktree.redbits.blocks;

import net.darktree.interference.api.RedstoneConnectable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class InverterBlock extends AbstractRedstoneGateBlock implements RedstoneConnectable {

	public InverterBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(POWERED, false));
	}

	@Override
	protected int getUpdateDelayInternal(BlockState state) {
		return 2;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED);
	}

	@Override
	protected boolean isValidInput(BlockState state) {
		return isRedstoneGate(state);
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		if (!state.get(POWERED)) {
			return state.get(FACING) == direction ? 15 : 0;
		} else {
			return 0;
		}
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (!state.get(POWERED)) {
			Direction direction = state.get(FACING);
			double d = (double) pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.2D;
			double e = (double) pos.getY() + 0.4D + (random.nextDouble() - 0.5D) * 0.2D;
			double f = (double) pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.2D;
			double h = (-5.0F / 16) * (float) direction.getOffsetX();
			double i = (-5.0F / 16) * (float) direction.getOffsetZ();
			world.addParticle(DustParticleEffect.DEFAULT, d + h, e, f + i, 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	public boolean connectsTo(BlockState state, Direction direction) {
		return state.get(RepeaterBlock.FACING).getAxis() == direction.getAxis();
	}

}
