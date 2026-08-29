package net.darktree.redbits.mixin;

import net.darktree.redbits.RedBits;
import net.darktree.redbits.blocks.ComplexPressurePlateBlock;
import net.darktree.redbits.utils.CampfireInventory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class SimpleItemTooltipsMixin {

	@Shadow
	public abstract Item getItem();

	@Inject(method = "addDetailsToTooltip", at = @At("HEAD"))
	public void appendTooltip(Item.TooltipContext context, TooltipDisplay displayComponent, @Nullable Player player, TooltipFlag type, Consumer<Component> textConsumer, CallbackInfo ci) {
		if (getItem() instanceof BlockItem blockItem) {
			if (blockItem.getBlock() instanceof ComplexPressurePlateBlock block) {
				textConsumer.accept(block.getTooltip());
			}
		}
	}

}
