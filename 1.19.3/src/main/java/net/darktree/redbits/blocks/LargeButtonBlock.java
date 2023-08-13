package net.darktree.redbits.blocks;

import net.minecraft.block.*;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class LargeButtonBlock extends ButtonBlock {

	public static final VoxelShape LARGE_CEILING_SHAPE = Block.createCuboidShape(4.0D, 14.0D, 4.0D, 12.0D, 16.0D, 12.0D);
	public static final VoxelShape LARGE_FLOOR_SHAPE = Block.createCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 2.0D, 12.0D);
	public static final VoxelShape LARGE_NORTH_SHAPE = Block.createCuboidShape(4.0D, 4.0D, 14.0D, 12.0D, 12.0D, 16.0D);
	public static final VoxelShape LARGE_SOUTH_SHAPE = Block.createCuboidShape(4.0D, 4.0D, 0.0D, 12.0D, 12.0D, 2.0D);
	public static final VoxelShape LARGE_WEST_SHAPE = Block.createCuboidShape(14.0D, 4.0D, 4.0D, 16.0D, 12.0D, 12.0D);
	public static final VoxelShape LARGE_EAST_SHAPE = Block.createCuboidShape(0.0D, 4.0D, 4.0D, 2.0D, 12.0D, 12.0D);
	public static final VoxelShape LARGE_CEILING_PRESSED_SHAPE = Block.createCuboidShape(4.0D, 15.0D, 4.0D, 12.0D, 16.0D, 12.0D);
	public static final VoxelShape LARGE_FLOOR_PRESSED_SHAPE = Block.createCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 1.0D, 12.0D);
	public static final VoxelShape LARGE_NORTH_PRESSED_SHAPE = Block.createCuboidShape(4.0D, 4.0D, 15.0D, 12.0D, 12.0D, 16.0D);
	public static final VoxelShape LARGE_SOUTH_PRESSED_SHAPE = Block.createCuboidShape(4.0D, 4.0D, 0.0D, 12.0D, 12.0D, 1.0D);
	public static final VoxelShape LARGE_WEST_PRESSED_SHAPE = Block.createCuboidShape(15.0D, 4.0D, 4.0D, 16.0D, 12.0D, 12.0D);
	public static final VoxelShape LARGE_EAST_PRESSED_SHAPE = Block.createCuboidShape(0.0D, 4.0D, 4.0D, 1.0D, 12.0D, 12.0D);

	public LargeButtonBlock(boolean wooden, AbstractBlock.Settings settings) {
		super(settings, 20, wooden, getSound(wooden, true), getSound(wooden, false));
	}

	private static SoundEvent getSound(boolean wooden, boolean powered) {
		if (wooden) {
			return powered ? SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_ON : SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_OFF;
		} else {
			return powered ? SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON : SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF;
		}
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		Direction direction = state.get(FACING);
		boolean powered = state.get(POWERED);

		// based on equally epic mojang code
		return switch (state.get(FACE)) {
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
