package net.darktree.redbits.blocks;

import net.darktree.redbits.utils.RedstoneConnectable;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RepeaterBlock;
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
import net.minecraft.world.BlockView;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;

import java.util.Random;

public class FlipFlopBlock extends AbstractRedstoneGateBlock implements RedstoneConnectable {

	public static final BooleanProperty INPUT = BooleanProperty.of("input");

	public FlipFlopBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(POWERED, false).with(INPUT, false));
	}

	@Override
	protected int getUpdateDelayInternal(BlockState state) {
		return 2;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED, INPUT);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if( player == null || player.getAbilities().allowModifyWorld ) {
			world.setBlockState( pos, state.with( POWERED, !state.get(POWERED)) );
			world.playSound( null, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 1.0f, 0.7f );
			return ActionResult.SUCCESS;
		}
		return super.onUse( state, world, pos, player, hand, hit );
	}

	@Override
	protected boolean isValidInput(BlockState state) {
		return isRedstoneGate(state);
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		if( state.get(POWERED) ) {
			return state.get(FACING) == direction ? 15 : 0;
		} else {
			return 0;
		}
	}

	@Override
	protected void updatePowered(World world, BlockPos pos, BlockState state) {
		boolean power = state.get(INPUT);
		boolean block = this.hasPower(world, pos, state);

		if (power != block && !world.getBlockTickScheduler().isTicking(pos, this)) {
			TickPriority tickPriority = TickPriority.HIGH;
			if (this.isTargetNotAligned(world, pos, state)) {
				tickPriority = TickPriority.EXTREMELY_HIGH;
			} else if (power) {
				tickPriority = TickPriority.VERY_HIGH;
			}

			world.getBlockTickScheduler().schedule(pos, this, this.getUpdateDelayInternal(state), tickPriority);
		}
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		boolean power = state.get(INPUT);
		boolean block = this.hasPower(world, pos, state);
		if( power && !block ) {
			world.setBlockState(pos, state.with(INPUT, false), 2);
		} else if( !power ) {
			world.setBlockState(pos, state.with(INPUT, true).with(POWERED, !state.get(POWERED)), 2);
			if (!block) {
				world.getBlockTickScheduler().schedule(pos, this, this.getUpdateDelayInternal(state), TickPriority.VERY_HIGH);
			}
		}
	}

	@Override
	public boolean connectsTo(BlockState state, Direction direction) {
		return state.get(RepeaterBlock.FACING).getAxis() == direction.getAxis();
	}
}
