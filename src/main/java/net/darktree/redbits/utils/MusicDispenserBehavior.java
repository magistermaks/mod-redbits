package net.darktree.redbits.utils;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class MusicDispenserBehavior {

    private FallibleItemDispenserBehavior musicDiscBehavior = new FallibleItemDispenserBehavior() {

        public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {

            Direction direction = pointer.getBlockState().get(DispenserBlock.FACING);
            BlockPos blockPos = pointer.getBlockPos().offset(direction);
            World world = pointer.getWorld();
            BlockState blockState = world.getBlockState(blockPos);
            this.setSuccess(true);

            if ( blockState.isOf(Blocks.JUKEBOX) ) {
                JukeboxInventory inventory = (JukeboxInventory) HopperBlockEntity.getInventoryAt(world, blockPos);
                if( inventory != null && inventory.canInsert( stack ) ) {
                    inventory.setStack( stack.split(1) );
                }

                return stack;
            } else {
                return super.dispenseSilently(pointer, stack);
            }

        }

    };

    public void register() {
        DispenserBlock.registerBehavior( Items.MUSIC_DISC_13, musicDiscBehavior );
        DispenserBlock.registerBehavior( Items.MUSIC_DISC_CAT, musicDiscBehavior );
        DispenserBlock.registerBehavior( Items.MUSIC_DISC_BLOCKS, musicDiscBehavior );
        DispenserBlock.registerBehavior( Items.MUSIC_DISC_CHIRP, musicDiscBehavior );
        DispenserBlock.registerBehavior( Items.MUSIC_DISC_FAR, musicDiscBehavior );
        DispenserBlock.registerBehavior( Items.MUSIC_DISC_MALL, musicDiscBehavior );
        DispenserBlock.registerBehavior( Items.MUSIC_DISC_MELLOHI, musicDiscBehavior );
        DispenserBlock.registerBehavior( Items.MUSIC_DISC_STAL, musicDiscBehavior );
        DispenserBlock.registerBehavior( Items.MUSIC_DISC_STRAD, musicDiscBehavior );
        DispenserBlock.registerBehavior( Items.MUSIC_DISC_WARD, musicDiscBehavior );
        DispenserBlock.registerBehavior( Items.MUSIC_DISC_11, musicDiscBehavior );
        DispenserBlock.registerBehavior( Items.MUSIC_DISC_WAIT, musicDiscBehavior );
        DispenserBlock.registerBehavior( Items.MUSIC_DISC_PIGSTEP, musicDiscBehavior );
    }

}
