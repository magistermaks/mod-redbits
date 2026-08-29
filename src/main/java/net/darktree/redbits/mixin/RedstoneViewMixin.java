package net.darktree.redbits.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.darktree.redbits.blocks.custom.CustomRedstoneGate;
import net.minecraft.block.BlockState;
import net.minecraft.world.RedstoneView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RedstoneView.class)
public interface RedstoneViewMixin {

	/**
	 * Make our custom gates able to lock repeaters
	 *
	 * getEmittedRedstonePower has a "onlyFromGate" boolean arguments, when that is true, it calls isRedstoneGate(),
	 * we need to patch it to also allow our CustomRedstoneGates. We can't just modify the isRedstoneGate() itself as
	 * it's also used by minecraft to check if we can get the FACING property from blockstate, ofc we could also patch
	 * that, but it sets a dangerous precedent and other mods may also use it for that, causing crashes (our custom
	 * gates don't have a FACING blockstate propery).
	 */
	@WrapOperation(
			method = "getEmittedRedstonePower(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Z)I",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/block/AbstractRedstoneGateBlock;isRedstoneGate(Lnet/minecraft/block/BlockState;)Z")
	)
	private boolean isRedstoneGateWrapper(BlockState state, Operation<Boolean> original) {
		return original.call(state) || (state.getBlock() instanceof CustomRedstoneGate);
	}

}
