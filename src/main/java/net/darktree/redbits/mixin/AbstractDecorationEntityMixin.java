package net.darktree.redbits.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.darktree.redbits.blocks.custom.CustomRedstoneGate;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HangingEntity.class)
public class AbstractDecorationEntityMixin {

	/**
	 * Make item frames attach to the custom gates (apparently it's something they do).
	 *
	 * We can't just modify the isRedstoneGate() itself as it's also used by minecraft to check if we can get the
	 * FACING property from blockstate, ofc we could also patch that, but it sets a dangerous precedent and other mods
	 * may also use it for that, causing crashes (our custom gates don't have a FACING blockstate propery).
	 */
	@WrapOperation(
			method = "method_59942", // lambda in canStayAttached()
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/DiodeBlock;isDiode(Lnet/minecraft/world/level/block/state/BlockState;)Z")
	)
	private boolean isRedstoneGateWrapper(BlockState state, Operation<Boolean> original) {
		return original.call(state) || (state.getBlock() instanceof CustomRedstoneGate);
	}


}
