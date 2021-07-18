package net.darktree.redbits.mixin;

import net.darktree.redbits.RedBits;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RedstoneTorchBlock.class)
abstract public class RedstoneTorchBlockMixin {

    @Inject(at = @At("HEAD"), method = "isBurnedOut(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Z)Z", cancellable = true)
    private static void isBurnedOut(World world, BlockPos pos, boolean addNew, CallbackInfoReturnable<Boolean> info) {
        if( RedBits.CONFIG.disable_burnout ) info.setReturnValue( false );
    }

}
