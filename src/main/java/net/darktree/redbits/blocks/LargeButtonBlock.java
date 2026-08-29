package net.darktree.redbits.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LargeButtonBlock extends ButtonBlock {

	public static final VoxelShape LARGE_CEILING_SHAPE = Block.box(4.0D, 14.0D, 4.0D, 12.0D, 16.0D, 12.0D);
	public static final VoxelShape LARGE_FLOOR_SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 2.0D, 12.0D);
	public static final VoxelShape LARGE_NORTH_SHAPE = Block.box(4.0D, 4.0D, 14.0D, 12.0D, 12.0D, 16.0D);
	public static final VoxelShape LARGE_SOUTH_SHAPE = Block.box(4.0D, 4.0D, 0.0D, 12.0D, 12.0D, 2.0D);
	public static final VoxelShape LARGE_WEST_SHAPE = Block.box(14.0D, 4.0D, 4.0D, 16.0D, 12.0D, 12.0D);
	public static final VoxelShape LARGE_EAST_SHAPE = Block.box(0.0D, 4.0D, 4.0D, 2.0D, 12.0D, 12.0D);
	public static final VoxelShape LARGE_CEILING_PRESSED_SHAPE = Block.box(4.0D, 15.0D, 4.0D, 12.0D, 16.0D, 12.0D);
	public static final VoxelShape LARGE_FLOOR_PRESSED_SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 1.0D, 12.0D);
	public static final VoxelShape LARGE_NORTH_PRESSED_SHAPE = Block.box(4.0D, 4.0D, 15.0D, 12.0D, 12.0D, 16.0D);
	public static final VoxelShape LARGE_SOUTH_PRESSED_SHAPE = Block.box(4.0D, 4.0D, 0.0D, 12.0D, 12.0D, 1.0D);
	public static final VoxelShape LARGE_WEST_PRESSED_SHAPE = Block.box(15.0D, 4.0D, 4.0D, 16.0D, 12.0D, 12.0D);
	public static final VoxelShape LARGE_EAST_PRESSED_SHAPE = Block.box(0.0D, 4.0D, 4.0D, 1.0D, 12.0D, 12.0D);

	public LargeButtonBlock(BlockSetType blockSetType, BlockBehaviour.Properties settings) {
		super(blockSetType, 20, settings);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		Direction direction = state.getValue(FACING);
		boolean powered = state.getValue(POWERED);

		// based on equally epic mojang code
		return switch (state.getValue(FACE)) {
			case FLOOR -> powered ? LARGE_FLOOR_PRESSED_SHAPE : LARGE_FLOOR_SHAPE;
			case WALL -> switch (direction) {
				case EAST -> powered ? LARGE_EAST_PRESSED_SHAPE : LARGE_EAST_SHAPE;
				case WEST -> powered ? LARGE_WEST_PRESSED_SHAPE : LARGE_WEST_SHAPE;
				case SOUTH -> powered ? LARGE_SOUTH_PRESSED_SHAPE : LARGE_SOUTH_SHAPE;
				default -> powered ? LARGE_NORTH_PRESSED_SHAPE : LARGE_NORTH_SHAPE;
			};
			default -> powered ? LARGE_CEILING_PRESSED_SHAPE : LARGE_CEILING_SHAPE;
		};
	}


}
