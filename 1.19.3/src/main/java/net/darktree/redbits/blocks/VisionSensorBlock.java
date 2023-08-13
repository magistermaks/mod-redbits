package net.darktree.redbits.blocks;

import net.darktree.interference.api.LookAtEvent;
import net.darktree.interference.api.RedstoneConnectable;
import net.darktree.redbits.RedBits;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

@SuppressWarnings("deprecation")
public class VisionSensorBlock extends Block implements RedstoneConnectable, LookAtEvent {

	public static final BooleanProperty POWERED = Properties.POWERED;

	public VisionSensorBlock(Settings settings) {
		super(settings);
		setDefaultState(this.stateManager.getDefaultState().with(POWERED, false));
	}

	public static void trigger(World world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);

		if (!state.get(POWERED)) {
			Block self = state.getBlock();

			if (!world.getBlockTickScheduler().isQueued(pos, self)) {
				world.setBlockState(pos, state.with(POWERED, true));
				world.scheduleBlockTick(pos, self, 2);
			}
		}
	}

	public void onLookAtStart(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		RedBits.LOOK_AT_PACKET.send(pos);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(POWERED);
	}

	@Override
	public boolean connectsTo(BlockState state, Direction direction) {
		return true;
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.get(POWERED) ? 15 : 0;
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		world.setBlockState(pos, state.with(POWERED, false));
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		world.scheduleBlockTick(pos, this, 2);
	}

}
