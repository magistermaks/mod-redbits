package net.darktree.redbits.blocks;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.phys.AABB;

public class ComplexPressurePlateBlock extends PressurePlateBlock {

	@FunctionalInterface
	public interface CollisionCondition {
		boolean call(Level world, AABB box);
	}

	private final CollisionCondition collisionCondition;

	public ComplexPressurePlateBlock(CollisionCondition condition, Properties settings) {
		super(BlockSetType.STONE, settings);
		this.collisionCondition = condition;
	}

	@Override
	protected int getSignalStrength(Level world, BlockPos pos) {
		return collisionCondition.call(world, TOUCH_AABB.move(pos)) ? 15 : 0;
	}

	public Component getTooltip() {
		return Component.translatable(this.getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GRAY);
	}

}
