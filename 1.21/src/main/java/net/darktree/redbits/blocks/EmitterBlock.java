package net.darktree.redbits.blocks;

import net.darktree.redbits.RedBits;
import net.minecraft.block.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.apache.commons.lang3.mutable.Mutable;

@SuppressWarnings("deprecation")
public class EmitterBlock extends Block {

	public static final IntProperty POWER = Properties.POWER;

	public EmitterBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(POWER, 1));
	}

	@Override
	public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
		ItemStack stack = super.getPickStack(world, pos, state);

		if (Screen.hasControlDown()) {
//			NbtCompound blockStateNbt = new NbtCompound();
//			blockStateNbt.putInt("power", state.get(EmitterBlock.POWER));
//			stack.setSubNbt(BlockItem.BLOCK_STATE_TAG_KEY, blockStateNbt);
//
//			NbtCompound displayNbt = new NbtCompound();
//			NbtList loreNbt = new NbtList();
//			loreNbt.add(NbtString.of("\"(+NBT)\""));
//			displayNbt.put("Lore", loreNbt);
//			stack.setSubNbt("display", displayNbt);
		}

		return stack;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(POWER);
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if (player == null || player.getAbilities().allowModifyWorld) {
			int power = interact(player, world, pos, state.get(POWER));
			world.setBlockState(pos, state.with(POWER, power));
			return ActionResult.SUCCESS;
		}

		return super.onUse(state, world, pos, player, hit);
	}

	private static boolean isConnected(World world, BlockPos pos) {

		Block center = world.getBlockState(pos).getBlock();

		if (center != RedBits.REDSTONE_EMITTER && center != Blocks.DETECTOR_RAIL) {
			return false;
		}

		for (Direction direction : Direction.Type.HORIZONTAL) {
			BlockPos side = pos.offset(direction);
			BlockState state = world.getBlockState(side);

			if (state.getBlock() != Blocks.COMPARATOR) {
				continue;
			}

			if (state.get(ComparatorBlock.FACING) == direction.getOpposite()) {
				return true;
			}
		}

		return false;

	}

	public static int interact(PlayerEntity player, World world, BlockPos pos, int power) {
		boolean decrement = player != null && player.isSneaking();
		power = power + (decrement ? -1 : 1);

		if (power < 0) power = 15;
		if (power > 15) power = 0;

		AbstractRedstoneGate.playClickSound(world, pos, RedBits.EMITTER_CLICK, decrement);

		if (player != null) {
			player.incrementStat(RedBits.INTERACT_WITH_REDSTONE_EMITTER);
			player.sendMessage(Text.translatable("message.redbits.power_level", power), true);

			// trigger then criterion when the connected comparator is powered by the emitter
			if (power != 0 && player instanceof ServerPlayerEntity serverPlayer && isConnected(world, pos)) {
				RedBits.USE_REDSTONE_EMITTER_CRITERION.trigger(serverPlayer);
			}
		}

		return power;
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return Math.max(state.get(POWER), world.getReceivedRedstonePower(pos));
	}

	@Override
	public boolean hasComparatorOutput(BlockState state) {
		return true;
	}

}
