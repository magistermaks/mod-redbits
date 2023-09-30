package net.darktree.redbits.blocks;

import net.darktree.redbits.RedBits;
import net.darktree.redbits.utils.FacingDirection;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;

import java.util.Random;

@SuppressWarnings("deprecation")
public class LatchBlock extends AbstractRedstoneGate {

	public static final EnumProperty<FacingDirection> POWER = EnumProperty.of("power", FacingDirection.class);
	public static final EnumProperty<Direction.Axis> AXIS = Properties.HORIZONTAL_AXIS;

	public LatchBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(AXIS, Direction.Axis.X).with(POWER, FacingDirection.FRONT));
	}

	protected boolean hasPower(World world, BlockPos pos, BlockState state, FacingDirection power) {
		return this.getPower(world, pos, state, power) > 0;
	}

	protected int getPower(World world, BlockPos pos, BlockState state, FacingDirection power) {
		Direction direction = Direction.from(state.get(AXIS), power.asAxisDirection());
		BlockPos blockPos = pos.offset(direction);
		return getInputPower(world, blockPos, direction);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(AXIS, POWER);
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.get(AXIS) == direction.getAxis() && state.get(POWER).asAxisDirection() == direction.getOpposite().getDirection() ? 15 : 0;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(AXIS, ctx.getPlayerFacing().getAxis()).with(POWER, FacingDirection.from(ctx.getPlayerFacing().getDirection()));
	}

	@Override
	public boolean connectsTo(BlockState state, Direction direction) {
		return state.get(AXIS) == direction.getAxis();
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		if (this.hasPower(world, pos, state, state.get(POWER).other())) {
			world.createAndScheduleBlockTick(pos, this, 1);
		}
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (player == null || player.getAbilities().allowModifyWorld) {
			FacingDirection facing = state.get(POWER);
			world.setBlockState(pos, state.with(POWER, facing.other()));

			AbstractRedstoneGate.playClickSound(world, pos, RedBits.LATCH_CLICK, facing.asBoolean());
			this.updatePowered(world, pos, state);
			return ActionResult.SUCCESS;
		}
		return super.onUse(state, world, pos, player, hand, hit);
	}

	@Override
	protected void updatePowered(World world, BlockPos pos, BlockState state) {
		boolean block = this.hasPower(world, pos, state, state.get(POWER).other());

		if (block && !world.getBlockTickScheduler().isTicking(pos, this)) {
			world.createAndScheduleBlockTick(pos, this, this.getUpdateDelayInternal(), TickPriority.HIGH);
		}
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		FacingDirection power = state.get(POWER).other();

		if (getPower(world, pos, state, power) > 0) {
			world.setBlockState(pos, state.with(POWER, power), 2);
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
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		Direction direction = Direction.from(state.get(AXIS), state.get(POWER).asAxisDirection()).rotateYClockwise();
		AbstractRedstoneGate.spawnSimpleParticles(DustParticleEffect.DEFAULT, world, pos, random, direction, false);
	}

}
