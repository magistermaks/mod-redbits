package net.darktree.redbits.blocks;

import net.darktree.redbits.RedBits;
import net.darktree.redbits.utils.TwoWayPower;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ObserverBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

import java.util.Random;

public class PowerObserverBlock extends ObserverBlock {

    // This class is cursed, don't look //

    public PowerObserverBlock(Settings settings) {
        super(settings);
    }

    public boolean isPoweredForState( BlockState state, ServerWorld world, BlockPos pos, Direction direction ) {

        try{
            if( state.get(Properties.POWERED) ) return true;
        }catch (IllegalArgumentException exception) {
            // ignored
        }

        try{
            if( state.get(Properties.POWER) > 0 ) return true;
        }catch (IllegalArgumentException exception) {
            // ignored
        }

        try{
            if( state.get(Properties.EXTENDED) ) return true;
        }catch (IllegalArgumentException exception) {
            // ignored
        }

        try{
            if( state.get(TwoWayRepeaterBlock.POWER) != TwoWayPower.NONE ) return true;
        }catch (IllegalArgumentException exception) {
            // ignored
        }

        if( (state.getBlock() == Blocks.REDSTONE_LAMP || state.getBlock() == RedBits.REDSTONE_LAMP) && state.get(Properties.LIT) ) return true;
        if( state.getBlock() == RedBits.RGB_LAMP && state.get(AnalogLampBlock.POWER) != 0 ) return true;
        return state.getBlock() == RedBits.LATCH;
    }

    protected void scheduleTick(WorldAccess world, BlockPos pos) {
        if (!world.isClient() && !world.getBlockTickScheduler().isScheduled(pos, this)) {
            world.getBlockTickScheduler().schedule(pos, this, 2);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {

        Direction direction = state.get(FACING);
        BlockPos target = pos.offset( direction );

        if ( isPoweredForState( world.getBlockState( target ), world, target, direction ) ) {
            world.setBlockState(pos, state.with(POWERED, true), 2);
        } else {
            world.setBlockState(pos, state.with(POWERED, false), 2);
        }

        this.updateNeighbors(world, pos, state);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        if ( state.get(FACING) == direction ) {
            this.scheduleTick(world, pos);
        }

        return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
    }

}
