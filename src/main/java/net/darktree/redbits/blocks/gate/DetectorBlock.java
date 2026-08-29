package net.darktree.redbits.blocks.gate;

import net.darktree.redbits.RedBits;
import net.darktree.redbits.blocks.custom.CustomRedstoneGate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.ticks.TickPriority;

public class DetectorBlock extends FlipFlopBlock {

	public static final BooleanProperty INVERTED = BooleanProperty.create("inverted");

	public DetectorBlock(Properties settings) {
		super(settings);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, false).setValue(INPUT, false).setValue(INVERTED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED, INPUT, INVERTED);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
		if (player == null || player.getAbilities().mayBuild) {
			boolean inverted = state.getValue(INVERTED);
			world.setBlockAndUpdate(pos, state.setValue(INVERTED, !inverted));
			CustomRedstoneGate.playClickSound(world, pos, RedBits.DETECTOR_CLICK, inverted);
			return InteractionResult.SUCCESS;
		}

		return super.useWithoutItem(state, world, pos, player, hit);
	}

	/**
	 * We override this here to make it NOT emit an update forward (oposite of FACING)
	 * when the output signal doesn't change, instead we call updateTarget() from scheduledTick()
	 */
	@Override
	protected void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
		// do nothing
	}

	private void updateState(ServerLevel world, BlockPos pos, BlockState next, boolean update) {
		world.setBlock(pos, next, Block.UPDATE_CLIENTS);

		if (update) {
			updateNeighborsInFront(world, pos, next);
		}
	}

	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {

		boolean powered = state.getValue(POWERED);
		boolean input = state.getValue(INPUT);
		boolean block = this.shouldTurnOn(world, pos, state);

		if (input && !block) {
			boolean output = state.getValue(INVERTED) || state.getValue(POWERED);
			BlockState next = state.setValue(INPUT, false).setValue(POWERED, output);
			world.setBlock(pos, next, Block.UPDATE_CLIENTS);
			updateState(world, pos, next, output != powered);
		} else if (!input && block) {
			boolean output = !state.getValue(INVERTED) || state.getValue(POWERED);
			BlockState next = state.setValue(INPUT, true).setValue(POWERED, output);
			updateState(world, pos, next, output != powered);
		} else if (powered) {
			updateState(world, pos, state.setValue(POWERED, false), true);
			return;
		} else {
			return;
		}

		world.scheduleTick(pos, this, this.getDelay(state), TickPriority.VERY_HIGH);
	}

}
