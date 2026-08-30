package net.darktree.redbits.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;

public class CampfireInventory extends SimpleContainer implements WorldlyContainer {

	private final LevelAccessor world;
	private final BlockPos pos;

	public CampfireInventory(LevelAccessor world, BlockPos pos) {
		super(4);
		this.world = world;
		this.pos = pos;
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[]{0, 1, 2, 3};
	}

	private CampfireBlockEntity getCampfireEntity() {
		CampfireBlockEntity entity = (CampfireBlockEntity) world.getBlockEntity(pos);

		if (entity == null) {
			throw new RuntimeException("[RedBits] Campfire inventory is not attached to Campfire block entity!");
		}

		return entity;
	}

	public Optional<RecipeHolder<CampfireCookingRecipe>> getRecipeFor(ItemStack stack) {
		if (world instanceof ServerLevelAccessor access) {
			ServerLevel server = access.getLevel();

			return server.recipeAccess().getRecipeFor(RecipeType.CAMPFIRE_COOKING, new SingleRecipeInput(stack), server);
		}

		return Optional.empty();
	}

	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction dir) {
		if (stack.getCount() != 1 || stack.isEmpty() || !getCampfireEntity().getItems().get(slot).isEmpty()) {
			return false;
		}

		return getRecipeFor(stack).isPresent();
	}

	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
		return false;
	}

	@Override
	public ItemStack getItem(int slot) {
		return getCampfireEntity().getItems().get(slot);
	}

	@Override
	public void clearContent() {
		getCampfireEntity().clearContent();
		this.setChanged();
	}

	@Override
	public String toString() {
		return toList().stream().filter((itemStack) -> !itemStack.isEmpty()).toList().toString();
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	public List<ItemStack> toList() {
		return new ArrayList<>(getCampfireEntity().getItems());
	}

	@Override
	public List<ItemStack> removeAllItems() {
		List<ItemStack> list = toList();
		this.clearContent();
		return list;
	}

	@Override
	public ItemStack removeItem(int slot, int amount) {
		ItemStack itemStack = ContainerHelper.removeItem(toList(), slot, amount);
		if (!itemStack.isEmpty()) {
			this.setChanged();
		}

		return itemStack;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		if (world instanceof ServerLevelAccessor access) {
			ServerLevel server = access.getLevel();

			Optional<RecipeHolder<CampfireCookingRecipe>> recipe = getRecipeFor(stack);
			recipe.ifPresent(cookingRecipe -> getCampfireEntity().placeFood(server, null, stack));
		}
	}

}
