package net.darktree.redbits.blocks;

import net.darktree.redbits.utils.ColorProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.redstone.Orientation;
import org.jetbrains.annotations.Nullable;

public class AnalogLampBlock extends Block {
	public static final ColorProperty POWER = ColorProperty.of("color");

	public AnalogLampBlock(Properties settings) {
		super(settings);
		this.registerDefaultState(this.defaultBlockState().setValue(POWER, 0));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return this.defaultBlockState().setValue(POWER, ctx.getLevel().getBestNeighborSignal(ctx.getClickedPos()));
	}

	@Override
	protected void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, @Nullable Orientation wireOrientation, boolean notify) {
		if (!world.isClientSide()) {
			int power = world.getBestNeighborSignal(pos);

			if (state.getValue(POWER) != power) {
				world.setBlock(pos, state.setValue(POWER, power), 2);
			}
		}
	}

	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		int power = world.getBestNeighborSignal(pos);

		if (state.getValue(POWER) != power) {
			world.setBlock(pos, state.setValue(POWER, power), 2);
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(POWER);
	}

}
