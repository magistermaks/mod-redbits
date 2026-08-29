package net.darktree.redbits.blocks;

import net.darktree.redbits.RedBits;
import net.darktree.redbits.blocks.custom.CustomRedstoneGate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComparatorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public class EmitterBlock extends Block {

	public static final IntegerProperty POWER = BlockStateProperties.POWER;

	public EmitterBlock(Properties settings) {
		super(settings);
		this.registerDefaultState(this.stateDefinition.any().setValue(POWER, 1));
	}

	@Override
	protected ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state, boolean includeData) {
		ItemStack stack = super.getCloneItemStack(world, pos, state, false);

		if (includeData) {
			stack.set(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY.with(POWER, state));
		}

		return stack;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(POWER);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
		if (player == null || player.getAbilities().mayBuild) {
			int power = interact(player, world, pos, state.getValue(POWER));
			world.setBlockAndUpdate(pos, state.setValue(POWER, power));
			return InteractionResult.SUCCESS;
		}

		return super.useWithoutItem(state, world, pos, player, hit);
	}

	private static boolean isConnected(Level world, BlockPos pos) {

		Block center = world.getBlockState(pos).getBlock();

		if (center != RedBits.REDSTONE_EMITTER && center != Blocks.DETECTOR_RAIL) {
			return false;
		}

		for (Direction direction : Direction.Plane.HORIZONTAL) {
			BlockPos side = pos.relative(direction);
			BlockState state = world.getBlockState(side);

			if (state.getBlock() != Blocks.COMPARATOR) {
				continue;
			}

			if (state.getValue(ComparatorBlock.FACING) == direction.getOpposite()) {
				return true;
			}
		}

		return false;

	}

	public static int interact(Player player, Level world, BlockPos pos, int power) {
		boolean decrement = player != null && player.isShiftKeyDown();
		power = power + (decrement ? -1 : 1);

		if (power < 0) power = 15;
		if (power > 15) power = 0;

		CustomRedstoneGate.playClickSound(world, pos, RedBits.EMITTER_CLICK, decrement);

		if (player != null) {
			player.awardStat(RedBits.INTERACT_WITH_REDSTONE_EMITTER);
			player.sendOverlayMessage(Component.translatable("message.redbits.power_level", power));

			// trigger then criterion when the connected comparator is powered by the emitter
			if (power != 0 && player instanceof ServerPlayer serverPlayer && isConnected(world, pos)) {
				RedBits.USE_REDSTONE_EMITTER_CRITERION.trigger(serverPlayer);
			}
		}

		return power;
	}

	@Override
	protected int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos, Direction direction) {
		return Math.max(state.getValue(POWER), world.getBestNeighborSignal(pos));
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

}
