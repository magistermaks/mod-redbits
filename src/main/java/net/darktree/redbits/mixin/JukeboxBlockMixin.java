package net.darktree.redbits.mixin;

import net.darktree.redbits.RedBits;
import net.minecraft.block.*;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JukeboxBlock.class)
abstract public class JukeboxBlockMixin extends BlockWithEntity {

    private static final BooleanProperty POWERED = Properties.POWERED;

    protected JukeboxBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V")
    private void init(AbstractBlock.Settings settings, CallbackInfo info) {
        setDefaultState(this.stateManager.getDefaultState().with(JukeboxBlock.HAS_RECORD, false).with(POWERED, false));
    }

    @Inject(at = @At("TAIL"), method = "appendProperties(Lnet/minecraft/state/StateManager$Builder;)V")
    public void appendProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo info) {
        builder.add(POWERED);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        if( !world.isClient ) {
            boolean power = world.isReceivingRedstonePower( pos );
            if( power ) {
                if( !state.get(POWERED) && state.get(JukeboxBlock.HAS_RECORD) ) {
                    if( RedBits.CONFIG.jukebox_integration) {
                        world.syncWorldEvent(WorldEvents.MUSIC_DISC_PLAYED, pos, 0);
                        world.createAndScheduleBlockTick(pos, this, 1);
                    }
                }
            }

            if( state.get(POWERED) != power ) {
                world.setBlockState( pos, state.with(POWERED, power) );
            }
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        JukeboxBlockEntity jukeboxBlockEntity = (JukeboxBlockEntity) world.getBlockEntity( pos );
        if( jukeboxBlockEntity != null ) {
            if( RedBits.CONFIG.jukebox_integration) {
                world.syncWorldEvent(WorldEvents.MUSIC_DISC_PLAYED, pos, Item.getRawId(jukeboxBlockEntity.getRecord().getItem()));
            }
        }else{
            RedBits.LOGGER.warn( "Unable to trigger sound event, as the given Jukebox doesn't have a BlockEntity attached!" );
        }
    }

}
