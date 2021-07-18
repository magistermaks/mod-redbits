package net.darktree.redbits.mixin;

import net.darktree.redbits.utils.RedstoneConnectable;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RedstoneWireBlock.class)
abstract public class RedstoneWireBlockMixin {

    @Inject(at = @At("HEAD"), method = "connectsTo(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;)Z", cancellable = true)
    private static void connectsTo(BlockState state, Direction dir, CallbackInfoReturnable<Boolean> info) {
        if( state.getBlock() instanceof RedstoneConnectable gate ) {
            info.setReturnValue( dir != null && gate.connectsTo(state, dir) );
        }
    }

}
