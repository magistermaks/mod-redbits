package net.darktree.redbits.mixin;

import net.darktree.redbits.RedBits;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.redstone.Orientation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(JukeboxBlock.class)
abstract public class JukeboxBlockMixin extends BaseEntityBlock {

	protected JukeboxBlockMixin(Properties settings) {
		super(settings);
	}

	@Unique
	private boolean verified = false;

	@Unique
	private boolean isValidTarget() {
		return this.getClass().equals(JukeboxBlock.class);
	}

	@Inject(at = @At("TAIL"), method = "<init>")
	private void init(BlockBehaviour.Properties settings, CallbackInfo info) {
		BlockState state = this.stateDefinition.any();

		if (isValidTarget() && state.hasProperty(BlockStateProperties.POWERED)) {
			registerDefaultState(state.setValue(JukeboxBlock.HAS_RECORD, false).setValue(BlockStateProperties.POWERED, false));
			verified = true;
		} else {
			RedBits.LOGGER.error("Skipped RedBits init in JukeboxBlockMixin, verify failed for: '" + this.getClass().getName() + "'. Was the class inherited from?");
		}
	}

	@Inject(at = @At("TAIL"), method = "createBlockStateDefinition")
	public void appendProperties(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo info) {
		if (isValidTarget()) {
			builder.add(BlockStateProperties.POWERED);
		}
	}

	@Inject(at = @At("HEAD"), method = "isSignalSource", cancellable = true)
	public void emitsRedstonePower(BlockState state, CallbackInfoReturnable<Boolean> cir) {
		if (RedBits.CONFIG.jukebox_integration && verified) {
			cir.setReturnValue(false);
		}
	}

	@Inject(at = @At("HEAD"), method = "getSignal", cancellable = true)
	public void getWeakRedstonePower(BlockState state, BlockGetter world, BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir) {
		if (RedBits.CONFIG.jukebox_integration && verified) {
			cir.setReturnValue(0);
		}
	}

	@Override
	protected void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, @Nullable Orientation wireOrientation, boolean notify) {
		if (!world.isClientSide() && verified) {
			boolean power = world.hasNeighborSignal(pos);

			if (power) {
				if (!state.getValue(BlockStateProperties.POWERED) && state.getValue(JukeboxBlock.HAS_RECORD)) {
					if (RedBits.CONFIG.jukebox_integration && world.getBlockEntity(pos) instanceof JukeboxBlockEntity entity) {
						entity.getSongPlayer().stop(world, null);
						world.scheduleTick(pos, this, 1);
					}
				}
			}

			if (state.getValue(BlockStateProperties.POWERED) != power) {
				world.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.POWERED, power));
			}
		}
	}

	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		if (RedBits.CONFIG.jukebox_integration && verified) {
			if (world.getBlockEntity(pos) instanceof JukeboxBlockEntity entity) {
				entity.tryForcePlaySong();
			}
		}
	}

}
