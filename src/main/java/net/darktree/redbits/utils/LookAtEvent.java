package net.darktree.redbits.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public interface LookAtEvent {

	/**
	 * Called when a player starts looking at a block
	 */
	default void onLookAtStart(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {}

	/**
	 * Called for every tick for every player that is looking at a block
	 */
	default void onLookAtTick(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {}

	/**
	 * Called when a player stops looking at a block
	 */
	default void onLookAtStop(BlockState state, Level world, BlockPos pos, Player player) {}

}
