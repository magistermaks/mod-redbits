package net.darktree.redbits.utils;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

public interface RedstoneConnectable {

    boolean connectsTo( BlockState state, Direction direction );

}
