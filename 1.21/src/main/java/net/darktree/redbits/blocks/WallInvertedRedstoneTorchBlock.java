package net.darktree.redbits.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.WallRedstoneTorchBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WallInvertedRedstoneTorchBlock extends WallRedstoneTorchBlock {

	public WallInvertedRedstoneTorchBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(LIT, false));
	}

	@Override
	protected boolean shouldUnpower(World world, BlockPos pos, BlockState state) {
		return !super.shouldUnpower(world, pos, state);
	}

}
