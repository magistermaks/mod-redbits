package net.darktree.redbits.utils;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface LookAtEvent {

	/**
	 * Called when a player starts looking at a block
	 */
	default void onLookAtStart(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {}

	/**
	 * Called for every tick for every player that is looking at a block
	 */
	default void onLookAtTick(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {}

	/**
	 * Called when a player stops looking at a block
	 */
	default void onLookAtStop(BlockState state, World world, BlockPos pos, PlayerEntity player) {}

}
