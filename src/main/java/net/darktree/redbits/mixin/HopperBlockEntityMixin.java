package net.darktree.redbits.mixin;

import net.darktree.redbits.utils.CampfireInventory;
import net.darktree.redbits.utils.JukeboxInventory;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HopperBlockEntity.class)
abstract public class HopperBlockEntityMixin {

	@Inject(at = @At("HEAD"), method = "getInventoryAt(Lnet/minecraft/world/World;DDD)Lnet/minecraft/inventory/Inventory;", cancellable = true)
	private static void getInventoryAt(World world, double x, double y, double z, CallbackInfoReturnable<Inventory> info) {
		BlockPos pos = new BlockPos( x, y, z );
		BlockState state = world.getBlockState(pos);

		if( state.getBlock() == Blocks.JUKEBOX ) {
			info.setReturnValue( new JukeboxInventory( world, pos ) );
		}

		if( state.getBlock() instanceof CampfireBlock) {
			info.setReturnValue( new CampfireInventory( world, pos ) );
		}

	}

}
