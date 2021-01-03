package net.darktree.redbits.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;

import java.util.Random;

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
        if( player == null || player.abilities.allowModifyWorld ) {
            world.setBlockState( pos, state.cycle(DELAY) );
            world.playSound( null, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 1.0f, 0.7f );
            return ActionResult.SUCCESS;
        }
        return super.onUse( state, world, pos, player, hand, hit );
    }

    @Override
    protected void updatePowered(World world, BlockPos pos, BlockState state) {
        if( hasPower(world, pos, state) ) {
            if( !state.get(INPUT) ) {
                world.getBlockTickScheduler().schedule(pos, this, getUpdateDelayInternal(state), TickPriority.HIGH);
            }
        }else{
            world.setBlockState(pos, state.with(INPUT, false), 2 );
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if( hasPower(world, pos, state) ) {
            if( state.get(INPUT) ) {
                world.setBlockState(pos, state.cycle(POWERED), 2 );
            }else{
                world.setBlockState(pos, state.with(INPUT, true), 2 );
            }

            world.getBlockTickScheduler().schedule(pos, this, (int) Math.pow( 2, state.get(DELAY) ), TickPriority.HIGH);
        }else{
            world.setBlockState(pos, state.with(INPUT, false).with(POWERED, false), 2 );
        }
    }

}
