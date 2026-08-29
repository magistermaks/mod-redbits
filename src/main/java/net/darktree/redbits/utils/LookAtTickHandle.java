package net.darktree.redbits.utils;

import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class LookAtTickHandle {

	public static void raytrace(Player player, BlockPoint previous, Consumer<BlockPoint> callback) {
		if (player.level() != null) {
			HitResult hit = player.pick(128, 0.0f, false);

			if (hit.getType() == HitResult.Type.BLOCK) {

				BlockHitResult blockHit = (BlockHitResult) hit;
				BlockPoint current = BlockPoint.of(player.level(), blockHit.getBlockPos());

				if (!current.equals(previous)) {

					if (current.block instanceof LookAtEvent handle) {
						handle.onLookAtStart(current.cached, current.world, current.pos, player, blockHit);
					}

					notifyPrevious(previous, player);
					callback.accept(current);

				}else{

					if (current.block instanceof LookAtEvent handle) {
						handle.onLookAtTick(current.cached, current.world, current.pos, player, blockHit);
					}

				}

			}else{

				notifyPrevious(previous, player);
				callback.accept(null);

			}

		}
	}

	private static void notifyPrevious(BlockPoint previous, Player player) {
		if( previous != null && previous.block instanceof LookAtEvent handle ) {
			handle.onLookAtStop(previous.query(), previous.world, previous.pos, player);
		}
	}

	public static class BlockPoint {

		public BlockPos pos;
		public Block block;
		public Level world;
		public BlockState cached;

		BlockPoint(BlockPos pos, Block block, Level world, BlockState state) {
			this.pos = pos;
			this.block = block;
			this.world = world;
			this.cached = state;
		}

		@Override
		public boolean equals(Object o) {
			if( this == o ) return true;
			if( !(o instanceof BlockPoint that) ) return false;
			return pos.equals(that.pos) && block.equals(that.block) && world.equals(that.world);
		}

		@Override
		public int hashCode() {
			return Objects.hash(pos, block, world);
		}

		public BlockState query() {
			return this.world.getBlockState(this.pos);
		}

		public static BlockPoint of(Level world, BlockPos pos) {
			BlockState state = world.getBlockState(pos);
			return new BlockPoint(pos, state.getBlock(), world, state);
		}

	}

}