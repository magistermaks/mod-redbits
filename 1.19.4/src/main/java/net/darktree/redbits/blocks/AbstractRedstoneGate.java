package net.darktree.redbits.blocks;

import net.darktree.interference.api.RedstoneConnectable;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

@SuppressWarnings("deprecation")
public abstract class AbstractRedstoneGate extends Block implements RedstoneConnectable {

	public static final VoxelShape SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);

	public AbstractRedstoneGate(Settings settings) {
		super(settings);
	}

	protected abstract void updateTarget(World world, BlockPos pos, BlockState state);

	protected abstract void updatePowered(World world, BlockPos pos, BlockState state);

	protected int getUpdateDelayInternal() {
		return 2;
	}

	public static void playClickSound(World world, BlockPos pos, SoundEvent sound, boolean pitch) {
		world.playSound(null, pos, sound, SoundCategory.BLOCKS, 0.3f, pitch ? 0.55f : 0.5f);
	}

	public int getInputPower(World world, BlockPos blockPos, Direction direction) {
		int i = world.getEmittedRedstonePower(blockPos, direction);

		if (i >= 15) {
			return i;
		} else {
			BlockState blockState = world.getBlockState(blockPos);
			return Math.max(i, blockState.isOf(Blocks.REDSTONE_WIRE) ? blockState.get(RedstoneWireBlock.POWER) : 0);
		}
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return hasTopRim(world, pos.down());
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.getWeakRedstonePower(world, pos, direction);
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	public boolean connectsTo(BlockState state, Direction direction) {
		return true;
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		this.updateTarget(world, pos, state);
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!moved && !state.isOf(newState.getBlock())) {
			super.onStateReplaced(state, world, pos, newState, false);
			this.updateTarget(world, pos, state);
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		if (state.canPlaceAt(world, pos)) {
			this.updatePowered(world, pos, state);
		} else {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			dropStacks(state, world, pos, blockEntity);
			world.removeBlock(pos, false);

			for (Direction direction : Direction.values()) {
				world.updateNeighborsAlways(pos.offset(direction), this);
			}
		}
	}

	public static void spawnSimpleParticles(ParticleEffect effect, World world, BlockPos pos, Random random, Direction facing, boolean server, float offset) {
		if (random.nextBoolean()) {
			return;
		}

		double d = (double) pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
		double e = (double) pos.getY() + 0.4 + (random.nextDouble() - 0.5) * 0.2;
		double f = (double) pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
		double h = (offset / 16) * (float) facing.getOffsetX();
		double i = (offset / 16) * (float) facing.getOffsetZ();

		if (!server) {
			world.addParticle(effect, d + h, e, f + i, 0, 0, 0);
		} else if (world instanceof ServerWorld serverWorld) {
			serverWorld.spawnParticles(effect, d + h, e, f + i, 1, 0, 0, 0, 0);
		}
	}
}
