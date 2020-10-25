package net.darktree.redbits.utils;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JukeboxInventory extends SimpleInventory implements SidedInventory {

    private final WorldAccess world;
    private final BlockPos pos;

    public JukeboxInventory( WorldAccess world, BlockPos pos ) {
        super(1);
        this.world = world;
        this.pos = pos;
    }

    public int getMaxCountPerStack() {
        return 1;
    }

    public static boolean isMusicDisc( ItemStack stack ) {
        if( stack.getCount() != 1 || stack.isEmpty() ) return false;
        return stack.getItem() instanceof MusicDiscItem;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[] {0};
    }

    public BlockState getJukebox() {
        BlockState bs = world.getBlockState(pos);
        if( bs.getBlock() != Blocks.JUKEBOX ) throw new RuntimeException( "[RedBits] Jukebox inventory is not attached to Jukebox block!" );
        return bs;
    }

    public JukeboxBlockEntity getJukeboxEntity() {
        JukeboxBlockEntity entity = (JukeboxBlockEntity) world.getBlockEntity(pos);
        if( entity == null ) throw new RuntimeException( "[RedBits] Jukebox inventory is not attached to Jukebox block entity!" );
        return entity;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        if( isMusicDisc(stack) ) {
            return !getJukebox().get(JukeboxBlock.HAS_RECORD);
        }
        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return getJukebox().get(JukeboxBlock.HAS_RECORD) && dir == Direction.DOWN;
    }

    public void markDirty() {
        boolean empty = this.isEmpty();
        world.setBlockState( pos, getJukebox().with( JukeboxBlock.HAS_RECORD, !empty ), 3 );

        if( empty && !world.isClient() ) {
            world.syncWorldEvent(1010, pos, 0);
        }
    }

    public ItemStack getStack() {
        return getJukeboxEntity().getRecord();
    }

    public void setStack(ItemStack stack) {
        getJukeboxEntity().setRecord( stack );
        this.markDirty();
    }

    public ItemStack getStack(int slot) {
        return getStack();
    }

    public void clear() {
        getJukeboxEntity().clear();
        this.markDirty();
    }

    public void provideRecipeInputs(RecipeFinder finder) {
        finder.addItem(getStack());
    }

    public String toString() {
        return toList().stream().filter((itemStack) -> !itemStack.isEmpty()).collect(Collectors.toList()).toString();
    }

    public boolean isEmpty() {
        return getStack().isEmpty();
    }

    public List<ItemStack> toList() {
        List<ItemStack> l = new ArrayList<>();
        l.add( getStack() );
        return l;
    }

    public List<ItemStack> clearToList() {
        List<ItemStack> list = toList();
        this.clear();
        return list;
    }

    public ItemStack removeStack(int slot, int amount) {
        ItemStack itemStack = Inventories.splitStack(toList(), slot, amount);
        if (!itemStack.isEmpty()) {
            this.markDirty();
        }

        return itemStack;
    }

    public boolean canInsert(ItemStack stack) {
        return canInsert( 1, stack, null );
    }

    public ItemStack removeStack(int slot) {
        ItemStack itemStack = getStack();
        if (itemStack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            setStack(ItemStack.EMPTY);
            return itemStack;
        }
    }

    public void setStack(int slot, ItemStack stack) {
        this.setStack(stack);
        this.markDirty();
    }

}
