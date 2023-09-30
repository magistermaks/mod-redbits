package net.darktree.redbits.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

@SuppressWarnings("deprecation")
public class ProjectorBlock extends AbstractRedstoneGate {

	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
	public static final IntProperty POWER = IntProperty.of("power", 0, 2);
	public static final BooleanProperty POWERED = Properties.POWERED;

	public ProjectorBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(POWER, 0).with(POWERED, false));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, POWER, POWERED);
	}

	@Override
	public boolean connectsTo(BlockState state, Direction direction) {
		return state.get(FACING).getAxis() == direction.getAxis();
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {

		Direction facing = state.get(FACING);
		Direction source = facing.getOpposite();
		int input = getInputPower(world, pos.offset(source), source);
		int output = state.get(POWER);

		BlockState next = state.with(POWERED, input > 0);
		boolean schedule = false;

		// handle input & ping
		if (input > 0) {
			BlockPos location = pos.offset(facing, input);
			BlockState target = world.getBlockState(location);

			if (target.getBlock() instanceof ProjectorBlock block) {
				block.ping(target, world, location);
				AbstractRedstoneGate.spawnSimpleParticles(DustParticleEffect.DEFAULT, world, pos, random, state.get(FACING).getOpposite(), true);
			} else {
				AbstractRedstoneGate.spawnSimpleParticles(ParticleTypes.SMOKE, world, pos, random, state.get(FACING).getOpposite(), true);
			}

			schedule = true;
		}

		// handle output
		if (output > 0) {
			next = next.with(POWER, output - 1);
			schedule = true;
		}

		if (schedule) {
			world.scheduleBlockTick(pos, this, getUpdateDelayInternal());
		}

		if (next != state) {
			world.setBlockState(pos, next, Block.NOTIFY_LISTENERS);
		}
	}

	private void ping(BlockState state, World world, BlockPos pos) {
		world.setBlockState(pos, state.with(POWER, 2), Block.NOTIFY_LISTENERS);
		world.scheduleBlockTick(pos, this, getUpdateDelayInternal());
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return direction == state.get(FACING).getOpposite() && state.get(POWER) != 0 ? 15 : 0;
	}

	@Override
	protected void updatePowered(World world, BlockPos pos, BlockState state) {
		world.scheduleBlockTick(pos, this, 1);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return getDefaultState().with(FACING, ctx.getPlayerFacing());
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		world.scheduleBlockTick(pos, this, 1);
	}

	@Override
	protected void updateTarget(World world, BlockPos pos, BlockState state) {
		Direction forward = state.get(FACING);
		Direction backward = forward.getOpposite();
		BlockPos front = pos.offset(forward);

		// updateNeighbor updates the block NEXT to the gate
		// and updateNeighborsExcept updates the neighbors of that block EXCEPT for the gate itself
		world.updateNeighbor(front, this, pos);
		world.updateNeighborsExcept(front, this, backward);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (state.get(POWER) > 0) {
			AbstractRedstoneGate.spawnSimpleParticles(DustParticleEffect.DEFAULT, world, pos, random, state.get(FACING).getOpposite(), false);
		}
	}

}
