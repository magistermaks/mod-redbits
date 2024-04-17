package net.darktree.redbits.mixin;

import net.darktree.redbits.RedBits;
import net.minecraft.block.*;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JukeboxBlock.class)
abstract public class JukeboxBlockMixin extends BlockWithEntity {

	protected JukeboxBlockMixin(Settings settings) {
		super(settings);
	}

	@Unique
	private boolean verified = false;

	@Unique
	private boolean isValidTarget() {
		return this.getClass().equals(JukeboxBlock.class);
	}

	@Inject(at = @At("TAIL"), method = "<init>")
	private void init(AbstractBlock.Settings settings, CallbackInfo info) {
		BlockState state = this.stateManager.getDefaultState();

		if (isValidTarget() && state.contains(Properties.POWERED)) {
			setDefaultState(state.with(JukeboxBlock.HAS_RECORD, false).with(Properties.POWERED, false));
			verified = true;
		} else {
			RedBits.LOGGER.error("Skipped RedBits init in JukeboxBlockMixin, verify failed for: '" + this.getClass().getName() + "'. Was the class inherited from?");
		}
	}

	@Inject(at = @At("TAIL"), method = "appendProperties")
	public void appendProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo info) {
		if (isValidTarget()) {
			builder.add(Properties.POWERED);
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		if (!world.isClient && verified) {
			boolean power = world.isReceivingRedstonePower(pos);
			if (power) {
				if (!state.get(Properties.POWERED) && state.get(JukeboxBlock.HAS_RECORD)) {
					if (RedBits.CONFIG.jukebox_integration) {
						world.syncWorldEvent(WorldEvents.MUSIC_DISC_PLAYED, pos, 0);
						world.scheduleBlockTick(pos, this, 1);
					}
				}
			}

			if (state.get(Properties.POWERED) != power) {
				world.setBlockState(pos, state.with(Properties.POWERED, power));
			}
		}
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (RedBits.CONFIG.jukebox_integration && verified) {
			JukeboxBlockEntity jukeboxBlockEntity = (JukeboxBlockEntity) world.getBlockEntity(pos);

			if (jukeboxBlockEntity != null) {
				world.syncWorldEvent(WorldEvents.MUSIC_DISC_PLAYED, pos, Item.getRawId(jukeboxBlockEntity.getRecord().getItem()));
			} else {
				RedBits.LOGGER.warn("Unable to trigger sound event, as the given Jukebox doesn't have a BlockEntity attached!");
			}
		}
	}

}
