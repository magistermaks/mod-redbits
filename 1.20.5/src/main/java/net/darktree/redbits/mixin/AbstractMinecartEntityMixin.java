package net.darktree.redbits.mixin;

import net.darktree.redbits.entity.EmitterMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractMinecartEntity.class)
public class AbstractMinecartEntityMixin {

	@Inject(method = "create", at = @At("HEAD"), cancellable = true)
	private static void create(ServerWorld world, double x, double y, double z, AbstractMinecartEntity.Type type, ItemStack stack, PlayerEntity player, CallbackInfoReturnable<AbstractMinecartEntity> info) {
		if (type == EmitterMinecartEntity.EMITTER) {
			info.setReturnValue(new EmitterMinecartEntity(world, x, y, z));
		}
	}

}
