package net.darktree.redbits.mixin;

import net.darktree.redbits.utils.RedstoneConnectable;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RedStoneWireBlock.class)
abstract public class RedstoneWireMixin {

	@Inject(at = @At("HEAD"), method = "shouldConnectTo(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Z", cancellable = true)
	private static void connectsTo(BlockState state, Direction dir, CallbackInfoReturnable<Boolean> info) {
		if (state.getBlock() instanceof RedstoneConnectable gate) {
			info.setReturnValue(dir != null && gate.connectsTo(state, dir));
		}
	}

}
