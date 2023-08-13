package net.darktree.redbits.blocks;

import net.darktree.redbits.RedBits;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;

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
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (player == null || player.getAbilities().allowModifyWorld) {
			boolean inverted = state.get(INVERTED);
			world.setBlockState(pos, state.with(INVERTED, !inverted));
			AbstractRedstoneGate.playClickSound(world, pos, RedBits.DETECTOR_CLICK, inverted);
			return ActionResult.SUCCESS;
		}
		return super.onUse(state, world, pos, player, hand, hit);
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {

		boolean input = state.get(INPUT);
		boolean block = this.hasPower(world, pos, state);

		if (input && !block) {
			world.setBlockState(pos, state.with(INPUT, false).with(POWERED, state.get(INVERTED) || state.get(POWERED)), 2);
		} else if (!input && block) {
			world.setBlockState(pos, state.with(INPUT, true).with(POWERED, !state.get(INVERTED) || state.get(POWERED)), 2);
		} else {
			world.setBlockState(pos, state.with(POWERED, false), 2);
			return;
		}

		world.createAndScheduleBlockTick(pos, this, this.getUpdateDelayInternal(state), TickPriority.VERY_HIGH);
	}

}
