package net.darktree.redbits.blocks.gate;

import net.darktree.redbits.RedBits;
import net.darktree.redbits.blocks.custom.CustomRedstoneGate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.tick.TickPriority;

public class DetectorBlock extends FlipFlopBlock {

	public static final BooleanProperty INVERTED = BooleanProperty.of("inverted");

	public DetectorBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(POWERED, false).with(INPUT, false).with(INVERTED, false));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED, INPUT, INVERTED);
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if (player == null || player.getAbilities().allowModifyWorld) {
			boolean inverted = state.get(INVERTED);
			world.setBlockState(pos, state.with(INVERTED, !inverted));
			CustomRedstoneGate.playClickSound(world, pos, RedBits.DETECTOR_CLICK, inverted);
			return ActionResult.SUCCESS;
		}

		return super.onUse(state, world, pos, player, hit);
	}

	/**
	 * We override this here to make it NOT emit an update forward (oposite of FACING)
	 * when the output signal doesn't change, instead we call updateTarget() from scheduledTick()
	 */
	@Override
	protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		// do nothing
	}

	private void updateState(ServerWorld world, BlockPos pos, BlockState next, boolean update) {
		world.setBlockState(pos, next, Block.NOTIFY_LISTENERS);

		if (update) {
			updateTarget(world, pos, next);
		}
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {

		boolean powered = state.get(POWERED);
		boolean input = state.get(INPUT);
		boolean block = this.hasPower(world, pos, state);

		if (input && !block) {
			boolean output = state.get(INVERTED) || state.get(POWERED);
			BlockState next = state.with(INPUT, false).with(POWERED, output);
			world.setBlockState(pos, next, Block.NOTIFY_LISTENERS);
			updateState(world, pos, next, output != powered);
		} else if (!input && block) {
			boolean output = !state.get(INVERTED) || state.get(POWERED);
			BlockState next = state.with(INPUT, true).with(POWERED, output);
			updateState(world, pos, next, output != powered);
		} else if (powered) {
			updateState(world, pos, state.with(POWERED, false), true);
			return;
		} else {
			return;
		}

		world.scheduleBlockTick(pos, this, this.getUpdateDelayInternal(state), TickPriority.VERY_HIGH);
	}

}
