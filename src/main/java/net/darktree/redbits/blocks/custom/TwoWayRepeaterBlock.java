package net.darktree.redbits.blocks.custom;

import net.darktree.redbits.utils.TwoWayPower;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.ticks.TickPriority;

public class TwoWayRepeaterBlock extends CustomRedstoneGate {

	public static final EnumProperty<TwoWayPower> POWER = EnumProperty.create("power", TwoWayPower.class);
	public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;

	public TwoWayRepeaterBlock(Properties settings) {
		super(settings);
		this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.X).setValue(POWER, TwoWayPower.NONE));
	}

	protected boolean hasPower(Level world, BlockPos pos, TwoWayPower power, Direction.Axis axis) {
		return TwoWayPower.getPower(world, pos, this, power, axis).hasPower();
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(AXIS, POWER);
	}

	@Override
	public boolean connectsTo(BlockState state, Direction direction) {
		return state.getValue(AXIS) == direction.getAxis();
	}

	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		BlockState newState = state;
		TwoWayPower power = state.getValue(POWER);
		Direction.Axis axis = state.getValue(AXIS);

		TwoWayPower.Unit next = TwoWayPower.getPower(world, pos, this, power, axis);
		boolean unlocked = power == TwoWayPower.NONE;

		if (!unlocked && next.hasPower()) {
			return;
		}

		if (unlocked && next.hasPower()) {
			newState = state.setValue(POWER, next.getDirection());
		}

		if (!next.hasPower()) {
			newState = state.setValue(POWER, TwoWayPower.NONE);
		}

		if (newState != state) {
			world.setBlock(pos, newState, 2);
		}
	}

	@Override
	public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {
		return state.getValue(AXIS) == direction.getAxis() && state.getValue(POWER).isAligned(direction) ? 15 : 0;
	}

	@Override
	protected void updatePowered(Level world, BlockPos pos, BlockState state) {
		boolean power = state.getValue(POWER) != TwoWayPower.NONE;
		boolean block = this.hasPower(world, pos, state.getValue(POWER), state.getValue(AXIS));

		if (power != block && !world.getBlockTicks().hasScheduledTick(pos, this)) {
			TickPriority priority = block ? TickPriority.VERY_HIGH : TickPriority.HIGH;
			world.scheduleTick(pos, this, this.getUpdateDelayInternal(), priority);
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return this.defaultBlockState().setValue(AXIS, ctx.getHorizontalDirection().getAxis());
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		if (this.hasPower(world, pos, state.getValue(POWER), state.getValue(AXIS))) {
			world.scheduleTick(pos, this, 1);
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

		// needed so the gate won't get stuck when there is a switch-back
		world.neighborChanged(pos, this, null);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
		TwoWayPower power = state.getValue(POWER);

		if (power != TwoWayPower.NONE) {
			CustomRedstoneGate.spawnSimpleParticles(DustParticleOptions.REDSTONE, world, pos, random, power.asDirection(state.getValue(AXIS)), false, -5);
		}
	}

}
