package net.darktree.redbits.utils;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface RedstoneConnectable {

	boolean connectsTo(BlockState state, @Nullable Direction dir);

}
