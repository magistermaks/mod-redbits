package net.darktree.redbits.blocks.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.Nullable;

public class ProjectorBlock extends CustomRedstoneGate {

	public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final IntegerProperty POWER = IntegerProperty.create("power", 0, 2);
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public ProjectorBlock(Properties settings) {
		super(settings);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWER, 0).setValue(POWERED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, POWER, POWERED);
	}

	@Override
	public boolean shouldRedstoneWireConnectTo(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
		return state.getValue(FACING).getAxis() == direction.getAxis();
	}

	/**
	 * We override this here to make it NOT emit an update forward
	 * when the output signal doesn't change, instead we call updateTarget() from scheduledTick()
	 */
	@Override
	protected void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
		// do nothing
	}

	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {

		Direction facing = state.getValue(FACING);
		Direction source = facing.getOpposite();
		int input = getInputPower(world, pos.relative(source), source);
		int output = state.getValue(POWER);
		boolean powered = state.getValue(POWERED);

		BlockState next = state;
		int flags = 0;
		boolean schedule = false;
		boolean update = false;

		// handle input & ping
		if (input > 0) {
			BlockPos location = pos.relative(facing, input);
			BlockState target = world.getBlockState(location);

			if (target.getBlock() instanceof ProjectorBlock block) {
				block.ping(target, world, location);
				CustomRedstoneGate.spawnSimpleParticles(DustParticleOptions.REDSTONE, world, pos, random, state.getValue(FACING).getOpposite(), true, -5);
			} else {
				CustomRedstoneGate.spawnSimpleParticles(ParticleTypes.SMOKE, world, pos, random, state.getValue(FACING).getOpposite(), true, -5);
			}

			if (!powered) {
				next = next.setValue(POWERED, input > 0);
				flags = Block.UPDATE_ALL;
			}

			schedule = true;
		}

		if (powered && input == 0) {
			next = next.setValue(POWERED, false);
			flags = Block.UPDATE_ALL;
		}

		// handle output
		if (output > 0) {
			next = next.setValue(POWER, output - 1);
			schedule = true;

			if (output == 1) {
				flags = Block.UPDATE_CLIENTS;
				update = true;
			}
		}

		if (schedule && !world.getBlockTicks().hasScheduledTick(pos, this)) {
			world.scheduleTick(pos, this, getUpdateDelayInternal());
		}

		if (next != state) {
			world.setBlock(pos, next, flags);
		}

		if (update) {
			updateTarget(world, pos, next);
		}
	}

	private void ping(BlockState state, Level world, BlockPos pos) {

		int power = state.getValue(POWER);
		int flags = power == 0 ? Block.UPDATE_CLIENTS : 0; // don't sync the 1->2 state change to clients

		BlockState next = state.setValue(POWER, 2);

		if (power != 2) {
			world.setBlock(pos, next, flags);
		}

		if (power == 0) {
			updateTarget(world, pos, next);
		}

		if (!world.getBlockTicks().hasScheduledTick(pos, this)) {
			world.scheduleTick(pos, this, getUpdateDelayInternal());
		}
	}

	@Override
	public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {
		return direction == state.getValue(FACING).getOpposite() && state.getValue(POWER) != 0 ? 15 : 0;
	}

	@Override
	protected void updatePowered(Level world, BlockPos pos, BlockState state) {
		world.scheduleTick(pos, this, 1);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection());
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		world.scheduleTick(pos, this, 1);
	}

	@Override
	protected void updateTarget(Level world, BlockPos pos, BlockState state) {
		Direction forward = state.getValue(FACING);
		Direction backward = forward.getOpposite();
		BlockPos front = pos.relative(forward);

		// updateNeighbor updates the block NEXT to the gate
		// and updateNeighborsExcept updates the neighbors of that block EXCEPT for the gate itself
		world.neighborChanged(front, this, null);
		world.updateNeighborsAtExceptFromFacing(front, this, backward, null);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
		if (state.getValue(POWER) > 0) {
			CustomRedstoneGate.spawnSimpleParticles(DustParticleOptions.REDSTONE, world, pos, random, state.getValue(FACING).getOpposite(), false, -5);
		}
	}

}
