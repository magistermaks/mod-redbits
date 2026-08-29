package net.darktree.redbits.utils;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public interface RedstoneConnectable {

	boolean connectsTo(BlockState state, @Nullable Direction dir);

}
