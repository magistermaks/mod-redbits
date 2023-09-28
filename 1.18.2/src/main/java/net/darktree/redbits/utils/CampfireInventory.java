package net.darktree.redbits.utils;

import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CampfireInventory extends SimpleInventory implements SidedInventory {

	private final WorldAccess world;
	private final BlockPos pos;

	public CampfireInventory(WorldAccess world, BlockPos pos) {
		super(4);
		this.world = world;
		this.pos = pos;
	}

	public int getMaxCountPerStack() {
		return 1;
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		return new int[]{0, 1, 2, 3};
	}

	private CampfireBlockEntity getCampfireEntity() {
		CampfireBlockEntity entity = (CampfireBlockEntity) world.getBlockEntity(pos);

		if (entity == null) {
			throw new RuntimeException("[RedBits] Campfire inventory is not attached to Campfire block entity!");
		}

		return entity;
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, Direction dir) {
		if (stack.getCount() != 1 || stack.isEmpty() || !getCampfireEntity().getItemsBeingCooked().get(slot).isEmpty()) {
			return false;
		}

		return getCampfireEntity().getRecipeFor(stack).isPresent();
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		return false;
	}

	public ItemStack getStack(int slot) {
		return getCampfireEntity().getItemsBeingCooked().get(slot);
	}

	public void clear() {
		getCampfireEntity().clear();
		this.markDirty();
	}

	public String toString() {
		return toList().stream().filter((itemStack) -> !itemStack.isEmpty()).collect(Collectors.toList()).toString();
	}

	public boolean isEmpty() {
		return false;
	}

	public List<ItemStack> toList() {
		return new ArrayList<>(getCampfireEntity().getItemsBeingCooked());
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
		if (stack.getCount() != 1 || stack.isEmpty()) return false;
		return getCampfireEntity().getRecipeFor(stack).isPresent();
	}

	public ItemStack removeStack(int slot) {
		return ItemStack.EMPTY;
	}

	public void setStack(int slot, ItemStack stack) {
		CampfireBlockEntity entity = getCampfireEntity();
		Optional<CampfireCookingRecipe> recipe = entity.getRecipeFor(stack);
		recipe.ifPresent(cookingRecipe -> getCampfireEntity().addItem(stack, cookingRecipe.getCookTime()));
	}

}
