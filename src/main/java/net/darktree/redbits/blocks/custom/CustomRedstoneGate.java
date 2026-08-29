package net.darktree.redbits.blocks.custom;

import net.darktree.redbits.utils.RedstoneConnectable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public abstract class CustomRedstoneGate extends Block implements RedstoneConnectable {

	public static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);

	public CustomRedstoneGate(Properties settings) {
		super(settings);
	}

	protected abstract void updateTarget(Level world, BlockPos pos, BlockState state);

	protected abstract void updatePowered(Level world, BlockPos pos, BlockState state);

	protected int getUpdateDelayInternal() {
		return 2;
	}

	public static void playClickSound(Level world, BlockPos pos, SoundEvent sound, boolean pitched) {
		world.playSound(null, pos, sound, SoundSource.BLOCKS, 0.3f, pitched ? 0.55f : 0.5f);
	}

	public int getInputPower(Level world, BlockPos blockPos, Direction direction) {
		int i = world.getSignal(blockPos, direction);

		if (i >= 15) {
			return i;
		} else {
			BlockState blockState = world.getBlockState(blockPos);
			return Math.max(i, blockState.is(Blocks.REDSTONE_WIRE) ? blockState.getValue(RedStoneWireBlock.POWER) : 0);
		}
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		return canSupportRigidBlock(world, pos.below());
	}

	@Override
	public int getDirectSignal(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {
		return state.getSignal(world, pos, direction);
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	public boolean connectsTo(BlockState state, Direction direction) {
		return true;
	}

	@Override
	protected void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
		this.updateTarget(world, pos, state);
	}

	@Override
	protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean moved) {
		if (!moved) {
			this.updateTarget(world, pos, state);
		}
	}

	@Override
	protected void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, @Nullable Orientation orientation, boolean notify) {
		if (state.canSurvive(world, pos)) {
			this.updatePowered(world, pos, state);
		} else {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			dropResources(state, world, pos, blockEntity);
			world.removeBlock(pos, false);

			for (Direction direction : Direction.values()) {
				world.updateNeighborsAt(pos.relative(direction), this);
			}
		}
	}

	protected InteractionResult onClicked(BlockState state, Level world, BlockPos pos) {
		return InteractionResult.PASS;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
		if (player == null || player.getAbilities().mayBuild) {
			return onClicked(state, world, pos);
		}

		return super.useWithoutItem(state, world, pos, player, hit);
	}

	public static void spawnSimpleParticles(ParticleOptions effect, Level world, BlockPos pos, RandomSource random, Direction facing, boolean server, float offset) {
		if (random.nextBoolean()) {
			return;
		}

		double d = (double) pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
		double e = (double) pos.getY() + 0.4 + (random.nextDouble() - 0.5) * 0.2;
		double f = (double) pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
		double h = (offset / 16f) * (float) facing.getStepX();
		double i = (offset / 16f) * (float) facing.getStepZ();

		if (!server) {
			world.addParticle(effect, d + h, e, f + i, 0, 0, 0);
		} else if (world instanceof ServerLevel serverWorld) {
			serverWorld.sendParticles(effect, d + h, e, f + i, 1, 0, 0, 0, 0);
		}
	}
}
