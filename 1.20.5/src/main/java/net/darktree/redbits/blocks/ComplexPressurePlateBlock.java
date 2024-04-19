package net.darktree.redbits.blocks;

import net.minecraft.block.BlockSetType;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.client.item.TooltipType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.List;

public class ComplexPressurePlateBlock extends PressurePlateBlock {

	@FunctionalInterface
	public interface CollisionCondition {
		boolean call(World world, Box box);
	}

	private final CollisionCondition collisionCondition;

	public ComplexPressurePlateBlock(CollisionCondition condition, Settings settings) {
		super(BlockSetType.STONE, settings);
		this.collisionCondition = condition;
	}

	@Override
	protected int getRedstoneOutput(World world, BlockPos pos) {
		return collisionCondition.call(world, BOX.offset(pos)) ? 15 : 0;
	}

	@Override
	public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
		tooltip.add(Text.translatable(this.getTranslationKey() + ".tooltip").formatted(Formatting.GRAY));
	}

}
