package net.darktree.redbits.blocks;

import net.darktree.redbits.RedBits;
import net.darktree.redbits.utils.LookAtEvent;
import net.darktree.redbits.utils.RedstoneConnectable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public class VisionSensorBlock extends Block implements RedstoneConnectable, LookAtEvent {

	public static final int DELAY = 8;
	public static final IntegerProperty POWER = IntegerProperty.create("power", 0, 2);

	public VisionSensorBlock(Properties settings) {
		super(settings);
		registerDefaultState(this.stateDefinition.any().setValue(POWER, 0));
	}

	public static void trigger(Level world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);

		if (state.getValue(POWER) == 0) {
			Block self = state.getBlock();

			if (!world.getBlockTicks().hasScheduledTick(pos, self)) {
				world.setBlockAndUpdate(pos, state.setValue(POWER, 2));
				world.scheduleTick(pos, self, DELAY);
			}
		}
	}

	@Override
	public void onLookAtStart(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
		RedBits.LOOK_AT_PACKET.send(pos);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(POWER);
	}

	@Override
	public boolean connectsTo(BlockState state, Direction direction) {
		return true;
	}

	@Override
	public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {
		return (state.getValue(POWER) != 0) ? 15 : 0;
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		int power = Math.max(0, state.getValue(POWER) - 1);

		world.setBlockAndUpdate(pos, state.setValue(POWER, power));

		if (power > 0) {
			world.scheduleTick(pos, this, DELAY);
		}
	}

	@Override
	public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
		if (oldState.getBlock() != state.getBlock()) {
			world.scheduleTick(pos, this, DELAY);
		}
	}

}
