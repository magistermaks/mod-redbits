package net.darktree.redbits.blocks;

import net.darktree.redbits.RedBits;
import net.darktree.redbits.utils.LookAtEvent;
import net.darktree.redbits.utils.RedstoneConnectable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class VisionSensorBlock extends Block implements RedstoneConnectable, LookAtEvent {

	public static final int DELAY = 8;
	public static final IntProperty POWER = IntProperty.of("power", 0, 2);

	public VisionSensorBlock(Settings settings) {
		super(settings);
		setDefaultState(this.stateManager.getDefaultState().with(POWER, 0));
	}

	public static void trigger(World world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);

		if (state.get(POWER) == 0) {
			Block self = state.getBlock();

			if (!world.getBlockTickScheduler().isQueued(pos, self)) {
				world.setBlockState(pos, state.with(POWER, 2));
				world.scheduleBlockTick(pos, self, DELAY);
			}
		}
	}

	@Override
	public void onLookAtStart(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		RedBits.LOOK_AT_PACKET.send(pos);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(POWER);
	}

	@Override
	public boolean connectsTo(BlockState state, Direction direction) {
		return true;
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return (state.get(POWER) != 0) ? 15 : 0;
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		int power = Math.max(0, state.get(POWER) - 1);

		world.setBlockState(pos, state.with(POWER, power));

		if (power > 0) {
			world.scheduleBlockTick(pos, this, DELAY);
		}
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		if (oldState.getBlock() != state.getBlock()) {
			world.scheduleBlockTick(pos, this, DELAY);
		}
	}

}
