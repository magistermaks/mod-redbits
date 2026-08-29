package net.darktree.redbits.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedstoneWallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;

public class WallInvertedRedstoneTorchBlock extends RedstoneWallTorchBlock {

	public WallInvertedRedstoneTorchBlock(Properties settings) {
		super(settings);
		this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false));
	}

	@Override
	protected boolean hasNeighborSignal(Level world, BlockPos pos, BlockState state) {
		return !super.hasNeighborSignal(world, pos, state);
	}

}
