package net.darktree.redbits.mixin;

import net.darktree.redbits.RedBits;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

	@Inject(method = "onRecipeCrafted", at = @At("HEAD"))
	private void onRecipeCrafted(Recipe<?> recipe, List<ItemStack> ingredients, CallbackInfo ci) {
		RedBits.RECIPE_CRAFTED_CRITERION.trigger((ServerPlayerEntity) (Object) this, recipe.getId());
	}

}
