package net.darktree.redbits.mixin;

import net.darktree.redbits.RedBits;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeUnlocker;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(RecipeUnlocker.class)
public interface RecipeUnlockerMixin {

	@Inject(method = "unlockLastRecipe", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/recipe/RecipeUnlocker;getLastRecipe()Lnet/minecraft/recipe/Recipe;"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void unlockLastRecipe(PlayerEntity player, CallbackInfo ci, Recipe recipe) {
		if (player instanceof ServerPlayerEntity serverPlayer && recipe != null) {
			RedBits.RECIPE_CRAFTED_CRITERION.trigger(serverPlayer, recipe.getId());
		}
	}

}
