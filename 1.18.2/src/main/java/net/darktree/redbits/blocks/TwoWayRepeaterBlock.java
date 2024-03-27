package net.darktree.redbits.blocks;

import net.darktree.redbits.utils.TwoWayPower;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;

import java.util.Random;

@SuppressWarnings("deprecation")
public class TwoWayRepeaterBlock extends AbstractRedstoneGate {

	public static final EnumProperty<TwoWayPower> POWER = EnumProperty.of("power", TwoWayPower.class);
	public static final EnumProperty<Direction.Axis> AXIS = Properties.HORIZONTAL_AXIS;

	public TwoWayRepeaterBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(AXIS, Direction.Axis.X).with(POWER, TwoWayPower.NONE));
	}

	protected boolean hasPower(World world, BlockPos pos, TwoWayPower power, Direction.Axis axis) {
		return TwoWayPower.getPower(world, pos, this, power, axis).hasPower();
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(AXIS, POWER);
	}

	@Override
	public boolean connectsTo(BlockState state, Direction direction) {
		return state.get(AXIS) == direction.getAxis();
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		BlockState newState = state;
		TwoWayPower power = state.get(POWER);
		Direction.Axis axis = state.get(AXIS);

		TwoWayPower.Unit next = TwoWayPower.getPower(world, pos, this, power, axis);
		boolean unlocked = power == TwoWayPower.NONE;

		if (!unlocked && next.hasPower()) {
			return;
		}

		if (unlocked && next.hasPower()) {
			newState = state.with(POWER, next.getDirection());
		}

		if (!next.hasPower()) {
			newState = state.with(POWER, TwoWayPower.NONE);
		}

		if (newState != state) {
			world.setBlockState(pos, newState, 2);
		}
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.get(AXIS) == direction.getAxis() && state.get(POWER).isAligned(direction) ? 15 : 0;
	}

	@Override
	protected void updatePowered(World world, BlockPos pos, BlockState state) {
		boolean power = state.get(POWER) != TwoWayPower.NONE;
		boolean block = this.hasPower(world, pos, state.get(POWER), state.get(AXIS));

		if (power != block && !world.getBlockTickScheduler().isQueued(pos, this)) {
			TickPriority priority = power ? TickPriority.VERY_HIGH : TickPriority.HIGH;
			world.createAndScheduleBlockTick(pos, this, this.getUpdateDelayInternal(), priority);
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(AXIS, ctx.getPlayerFacing().getAxis());
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		if (this.hasPower(world, pos, state.get(POWER), state.get(AXIS))) {
			world.createAndScheduleBlockTick(pos, this, 1);
		}
	}

	@Override
	protected void updateTarget(World world, BlockPos pos, BlockState state) {
		Direction forward = Direction.from(state.get(AXIS), Direction.AxisDirection.POSITIVE);
		Direction backward = forward.getOpposite();

		BlockPos front = pos.offset(forward);
		BlockPos back = pos.offset(backward);

		// updateNeighbor updates the block NEXT to the gate
		// and updateNeighborsExcept updates the neighbors of that block EXCEPT for the gate itself
		world.updateNeighbor(front, this, pos);
		world.updateNeighborsExcept(front, this, backward);

		// do the same for the other end of the gate
		world.updateNeighbor(back, this, pos);
		world.updateNeighborsExcept(back, this, forward);

		// needed so the gate won't get stuck when there is a switch-back
		world.updateNeighbor(pos, this, pos);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		TwoWayPower power = state.get(POWER);

		if (power != TwoWayPower.NONE) {
			AbstractRedstoneGate.spawnSimpleParticles(DustParticleEffect.DEFAULT, world, pos, random, power.asDirection(state.get(AXIS)), false, -5);
		}
	}

}
