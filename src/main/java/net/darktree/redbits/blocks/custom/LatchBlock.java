package net.darktree.redbits.blocks.custom;

import net.darktree.redbits.RedBits;
import net.darktree.redbits.utils.FacingDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.ticks.TickPriority;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class LatchBlock extends CustomRedstoneGate {

	public static final EnumProperty<FacingDirection> POWER = EnumProperty.create("power", FacingDirection.class);
	public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;

	public LatchBlock(Properties settings) {
		super(settings);
		this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.X).setValue(POWER, FacingDirection.FRONT));
	}

	protected boolean hasPower(Level world, BlockPos pos, BlockState state, FacingDirection power) {
		return this.getPower(world, pos, state, power) > 0;
	}

	protected int getPower(Level world, BlockPos pos, BlockState state, FacingDirection power) {
		Direction direction = Direction.fromAxisAndDirection(state.getValue(AXIS), power.asAxisDirection());
		BlockPos blockPos = pos.relative(direction);
		return getInputPower(world, blockPos, direction);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(AXIS, POWER);
	}

	@Override
	public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {
		return state.getValue(AXIS) == direction.getAxis() && state.getValue(POWER).asAxisDirection() == direction.getOpposite().getAxisDirection() ? 15 : 0;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		Direction facing = ctx.getHorizontalDirection();
		return this.defaultBlockState().setValue(AXIS, facing.getAxis()).setValue(POWER, FacingDirection.from(facing.getAxisDirection()));
	}

	@Override
	public boolean shouldRedstoneWireConnectTo(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
		return state.getValue(AXIS) == direction.getAxis();
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		if (this.hasPower(world, pos, state, state.getValue(POWER).other())) {
			world.scheduleTick(pos, this, 1);
		}
	}

	@Override
	protected InteractionResult onClicked(BlockState state, Level world, BlockPos pos) {
		FacingDirection facing = state.getValue(POWER);
		BlockState next = state.setValue(POWER, facing.other());

		world.setBlockAndUpdate(pos, next);

		CustomRedstoneGate.playClickSound(world, pos, RedBits.LATCH_CLICK, facing.asBoolean());
		this.updatePowered(world, pos, next);
		return InteractionResult.SUCCESS;
	}

	@Override
	protected void updatePowered(Level world, BlockPos pos, BlockState state) {
		boolean block = this.hasPower(world, pos, state, state.getValue(POWER).other());

		if (block && !world.getBlockTicks().hasScheduledTick(pos, this)) {
			world.scheduleTick(pos, this, this.getUpdateDelayInternal(), TickPriority.HIGH);
		}
	}

	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		FacingDirection power = state.getValue(POWER).other();

		if (getPower(world, pos, state, power) > 0) {
			world.setBlock(pos, state.setValue(POWER, power), 2);
		}
	}

	@Override
	protected void updateTarget(Level world, BlockPos pos, BlockState state) {
		Direction forward = Direction.fromAxisAndDirection(state.getValue(AXIS), Direction.AxisDirection.POSITIVE);
		Direction backward = forward.getOpposite();

		BlockPos front = pos.relative(forward);
		BlockPos back = pos.relative(backward);

		// updateNeighbor updates the block NEXT to the gate
		// and updateNeighborsExcept updates the neighbors of that block EXCEPT for the gate itself
		world.neighborChanged(front, this, null);
		world.updateNeighborsAtExceptFromFacing(front, this, backward, null);

		// do the same for the other end of the gate
		world.neighborChanged(back, this, null);
		world.updateNeighborsAtExceptFromFacing(back, this, forward, null);
	}

	@Override
	public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
		Direction direction = Direction.fromAxisAndDirection(state.getValue(AXIS), state.getValue(POWER).asAxisDirection()).getClockWise();
		CustomRedstoneGate.spawnSimpleParticles(DustParticleOptions.REDSTONE, world, pos, random, direction, false, -5);
	}

}
