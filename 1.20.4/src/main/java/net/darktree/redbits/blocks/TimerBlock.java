package net.darktree.redbits.blocks;

import net.darktree.redbits.RedBits;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.tick.TickPriority;

public class TimerBlock extends FlipFlopBlock {

	public static final IntProperty DELAY = IntProperty.of("delay", 1, 4);

	public TimerBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(POWERED, false).with(INPUT, false).with(DELAY, 1));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED, INPUT, DELAY);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (player == null || player.getAbilities().allowModifyWorld) {
			world.setBlockState(pos, state.cycle(DELAY));

			AbstractRedstoneGate.playClickSound(world, pos, RedBits.TIMER_CLICK, true);
			return ActionResult.SUCCESS;
		}
		return super.onUse(state, world, pos, player, hand, hit);
	}

	@Override
	protected void updatePowered(World world, BlockPos pos, BlockState state) {
		if (hasPower(world, pos, state)) {
			if (!state.get(INPUT)) {
				world.scheduleBlockTick(pos, this, getUpdateDelayInternal(state), TickPriority.HIGH);
			}
		} else {
			world.setBlockState(pos, state.with(INPUT, false), 2);
		}
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (hasPower(world, pos, state)) {
			if (state.get(INPUT)) {
				world.setBlockState(pos, state.cycle(POWERED), 2);
			} else {
				world.setBlockState(pos, state.with(INPUT, true), 2);
			}

			world.scheduleBlockTick(pos, this, (int) Math.pow(2, state.get(DELAY)), TickPriority.HIGH);
		} else {
			world.setBlockState(pos, state.with(INPUT, false).with(POWERED, false), 2);
		}
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (state.get(POWERED)) {
			AbstractRedstoneGate.spawnSimpleParticles(DustParticleEffect.DEFAULT, world, pos, random, state.get(FACING), false, -5);
		}
	}

}
