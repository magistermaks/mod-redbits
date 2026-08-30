package net.darktree.redbits.blocks.gate;

import com.mojang.serialization.MapCodec;
import net.darktree.redbits.RedBits;
import net.darktree.redbits.blocks.custom.CustomRedstoneGate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.ticks.TickPriority;
import org.jetbrains.annotations.Nullable;

public class FlipFlopBlock extends DiodeBlock {

	public static final BooleanProperty INPUT = BooleanProperty.create("input");

	public FlipFlopBlock(Properties settings) {
		super(settings);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, false).setValue(INPUT, false));
	}

	@Override
	protected int getDelay(BlockState state) {
		return 2;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED, INPUT);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
		if (player == null || player.getAbilities().mayBuild) {
			boolean powered = state.getValue(POWERED);
			world.setBlockAndUpdate(pos, state.setValue(POWERED, !powered));

			CustomRedstoneGate.playClickSound(world, pos, RedBits.FLIP_FLOP_CLICK, powered);
			return InteractionResult.SUCCESS;
		}
		return super.useWithoutItem(state, world, pos, player, hit);
	}

	@Override
	public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {
		if (state.getValue(POWERED)) {
			return state.getValue(FACING) == direction ? 15 : 0;
		} else {
			return 0;
		}
	}

	@Override
	protected void checkTickOnNeighbor(Level world, BlockPos pos, BlockState state) {
		boolean power = state.getValue(INPUT);
		boolean block = this.shouldTurnOn(world, pos, state);

		if (power != block && !world.getBlockTicks().hasScheduledTick(pos, this)) {
			TickPriority tickPriority = TickPriority.HIGH;

			if (this.shouldPrioritize(world, pos, state)) {
				tickPriority = TickPriority.EXTREMELY_HIGH;
			} else if (block) {
				tickPriority = TickPriority.VERY_HIGH;
			}

			world.scheduleTick(pos, this, this.getDelay(state), tickPriority);
		}
	}

	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		boolean power = state.getValue(INPUT);
		boolean block = this.shouldTurnOn(world, pos, state);
		if (power && !block) {
			world.setBlock(pos, state.setValue(INPUT, false), 2);
		} else if (!power) {
			world.setBlock(pos, state.setValue(INPUT, true).setValue(POWERED, !state.getValue(POWERED)), Block.UPDATE_CLIENTS);

			if (!block) {
				world.scheduleTick(pos, this, this.getDelay(state), TickPriority.VERY_HIGH);
			}
		}
	}

	@Override
	public boolean shouldRedstoneWireConnectTo(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
		return state.getValue(RepeaterBlock.FACING).getAxis() == direction.getAxis();
	}

}
