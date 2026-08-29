package net.darktree.redbits.blocks.gate;

import com.mojang.serialization.MapCodec;
import net.darktree.redbits.blocks.custom.CustomRedstoneGate;
import net.darktree.redbits.utils.RedstoneConnectable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class InverterBlock extends DiodeBlock implements RedstoneConnectable {

	public InverterBlock(Properties settings) {
		super(settings);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, false));
	}

	@Override
	protected int getDelay(BlockState state) {
		return 2;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED);
	}

	@Override
	public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {
		if (!state.getValue(POWERED)) {
			return state.getValue(FACING) == direction ? 15 : 0;
		} else {
			return 0;
		}
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
		if (!state.getValue(POWERED)) {
			CustomRedstoneGate.spawnSimpleParticles(DustParticleOptions.REDSTONE, world, pos, random, state.getValue(FACING), false, -1);
		}
	}

	@Override
	public boolean connectsTo(BlockState state, Direction direction) {
		return state.getValue(RepeaterBlock.FACING).getAxis() == direction.getAxis();
	}

	@Override
	protected MapCodec<? extends DiodeBlock> codec() {
		return null;
	}

}
