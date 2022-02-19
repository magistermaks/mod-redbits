package net.darktree.redbits.blocks;

import net.darktree.redbits.RedBits;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EmitterBlock extends Block {

    public static final IntProperty POWER = Properties.POWER;

    public EmitterBlock( Settings settings ) {
        super( settings );
        this.setDefaultState( this.stateManager.getDefaultState().with(POWER, 1) );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWER);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(player == null || player.getAbilities().allowModifyWorld) {
            int power = interact(player, world, pos, state.get(POWER));
            world.setBlockState(pos, state.with(POWER, power));
            return ActionResult.SUCCESS;
        }
        return super.onUse( state, world, pos, player, hand, hit );
    }

    public static int interact(PlayerEntity player, World world, BlockPos pos, int power) {
        power = power < 15 ? power + 1 : 0;

        world.playSound(null, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 1.0f, 0.7f);

        if (player != null) {
            player.incrementStat(RedBits.INTERACT_WITH_REDSTONE_EMITTER);
            player.sendMessage(new TranslatableText("message.redbits.power_level", power), true);
        }
        
        return power;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return Math.max( state.get(POWER), world.getReceivedRedstonePower( pos ) );
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

}
