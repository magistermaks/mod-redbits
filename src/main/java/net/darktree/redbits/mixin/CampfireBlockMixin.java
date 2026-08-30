package net.darktree.redbits.mixin;

import net.darktree.redbits.RedBits;
import net.darktree.redbits.utils.CampfireInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CampfireBlock.class)
public abstract class CampfireBlockMixin extends BaseEntityBlock implements WorldlyContainerHolder {

	protected CampfireBlockMixin(Properties settings) {
		super(settings);
	}

	@Override
	public WorldlyContainer getContainer(BlockState state, LevelAccessor world, BlockPos pos) {
		if (RedBits.CONFIG.campfire_integration && state.getBlock().getClass().equals(CampfireBlock.class)) {
			return new CampfireInventory(world, pos);
		}

		return null;
	}

}
