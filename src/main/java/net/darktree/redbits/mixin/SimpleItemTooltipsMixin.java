package net.darktree.redbits.mixin;

import net.darktree.redbits.RedBits;
import net.darktree.redbits.blocks.ComplexPressurePlateBlock;
import net.darktree.redbits.utils.CampfireInventory;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.InventoryProvider;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
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

	@Inject(method = "appendTooltip", at = @At("HEAD"))
	public void appendTooltip(Item.TooltipContext context, TooltipDisplayComponent displayComponent, @Nullable PlayerEntity player, TooltipType type, Consumer<Text> textConsumer, CallbackInfo ci) {
		if (getItem() instanceof BlockItem blockItem) {
			if (blockItem.getBlock() instanceof ComplexPressurePlateBlock block) {
				textConsumer.accept(block.getTooltip());
			}
		}
	}

}
