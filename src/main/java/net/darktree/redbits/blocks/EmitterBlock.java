package net.darktree.redbits.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class EmitterBlock extends Block {

    public static final IntProperty POWER = Properties.POWER;

    public EmitterBlock() {
        super( Settings.of(Material.METAL, MaterialColor.LAVA)
                .requiresTool()
                .strength(5.0F, 6.0F)
                .sounds(BlockSoundGroup.METAL)
                .solidBlock( (BlockState state, BlockView world, BlockPos pos) -> true ) );

        this.setDefaultState( this.stateManager.getDefaultState().with(POWER, 1) );
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWER);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if( player == null || player.abilities.allowModifyWorld ) {
            int i = state.get(POWER);
            world.setBlockState( pos, state.with( POWER, i < 15 ? i + 1 : 0 ) );
            world.playSound( null, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 1.0f, 0.7f );
            return ActionResult.SUCCESS;
        }
        return super.onUse( state, world, pos, player, hand, hit );
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
