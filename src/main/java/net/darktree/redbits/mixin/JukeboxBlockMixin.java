package net.darktree.redbits.mixin;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(JukeboxBlock.class)
abstract public class JukeboxBlockMixin extends BlockWithEntity {

    private static final BooleanProperty POWERED = Properties.POWERED;

    protected JukeboxBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V")
    private void init(AbstractBlock.Settings settings, CallbackInfo info) {
        this.setDefaultState(this.stateManager.getDefaultState().with(JukeboxBlock.HAS_RECORD, false).with(POWERED, false));
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
                    world.syncWorldEvent(1010, pos, 0);
                    world.getBlockTickScheduler().schedule(pos, this, 1);
                }
            }

            if( state.get(POWERED) != power ) {
                world.setBlockState( pos, state.with(POWERED, power) );
            }
        }
    }

    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        JukeboxBlockEntity jukeboxBlockEntity = (JukeboxBlockEntity) world.getBlockEntity( pos );
        if( jukeboxBlockEntity != null ) {
            world.syncWorldEvent(1010, pos, Item.getRawId(jukeboxBlockEntity.getRecord().getItem()));
        }else{
            LOGGER.warn( "[RedBits] Unable to trigger sound event, as the given Jukebox don't have a BlockEntity attached!" );
        }
    }

    @Shadow
    abstract public BlockEntity createBlockEntity(BlockView world);

}
