package net.darktree.redbits.blocks.gate;

import net.darktree.redbits.RedBits;
import net.darktree.redbits.blocks.custom.CustomRedstoneGate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.ticks.TickPriority;

public class TimerBlock extends FlipFlopBlock {

	public static final IntegerProperty DELAY = IntegerProperty.create("delay", 1, 4);

	public TimerBlock(Properties settings) {
		super(settings);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, false).setValue(INPUT, false).setValue(DELAY, 1));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED, INPUT, DELAY);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
		if (player == null || player.getAbilities().mayBuild) {
			world.setBlockAndUpdate(pos, state.cycle(DELAY));

			CustomRedstoneGate.playClickSound(world, pos, RedBits.TIMER_CLICK, true);
			return InteractionResult.SUCCESS;
		}

		return super.useWithoutItem(state, world, pos, player, hit);
	}

	@Override
	protected void checkTickOnNeighbor(Level world, BlockPos pos, BlockState state) {
		if (shouldTurnOn(world, pos, state)) {
			if (!state.getValue(INPUT)) {
				world.scheduleTick(pos, this, getDelay(state), TickPriority.HIGH);
			}
		} else {
			world.setBlock(pos, state.setValue(INPUT, false), Block.UPDATE_CLIENTS);
		}
	}

	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		if (shouldTurnOn(world, pos, state)) {
			if (state.getValue(INPUT)) {
				world.setBlock(pos, state.cycle(POWERED), Block.UPDATE_CLIENTS);
			} else {
				world.setBlock(pos, state.setValue(INPUT, true), Block.UPDATE_CLIENTS);
			}

			world.scheduleTick(pos, this, (int) Math.pow(2, state.getValue(DELAY)), TickPriority.HIGH);
		} else {
			world.setBlock(pos, state.setValue(INPUT, false).setValue(POWERED, false), Block.UPDATE_CLIENTS);
		}
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
		if (state.getValue(POWERED)) {
			CustomRedstoneGate.spawnSimpleParticles(DustParticleOptions.REDSTONE, world, pos, random, state.getValue(FACING), false, -5);
		}
	}

}
