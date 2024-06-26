package net.darktree.redbits.mixin;

import net.darktree.redbits.RedBits;
import net.darktree.redbits.utils.CampfireInventory;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.InventoryProvider;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CampfireBlock.class)
public abstract class CampfireBlockMixin extends BlockWithEntity implements InventoryProvider {

	protected CampfireBlockMixin(Settings settings) {
		super(settings);
	}

	@Override
	public SidedInventory getInventory(BlockState state, WorldAccess world, BlockPos pos) {
		if (RedBits.CONFIG.campfire_integration && state.getClass().equals(CampfireBlock.class)) {
			return new CampfireInventory(world, pos);
		}

		return null;
	}

}
