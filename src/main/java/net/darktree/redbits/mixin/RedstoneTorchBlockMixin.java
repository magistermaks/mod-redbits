package net.darktree.redbits.mixin;

import net.darktree.redbits.RedBits;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RedstoneTorchBlock.class)
abstract public class RedstoneTorchBlockMixin {

    @Inject(at = @At("HEAD"), method = "isToggledTooFrequently", cancellable = true)
    private static void isBurnedOut(Level world, BlockPos pos, boolean addNew, CallbackInfoReturnable<Boolean> info) {
        if (RedBits.CONFIG.disable_burnout) {
            info.setReturnValue(false);
        }
    }

}
