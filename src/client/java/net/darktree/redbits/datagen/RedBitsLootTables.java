package net.darktree.redbits.datagen;

import net.darktree.redbits.RedBits;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Block;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RedBitsLootTables extends FabricBlockLootSubProvider {

	public RedBitsLootTables(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	private List<Block> getBlocks(Class<?> clazz) {
		ArrayList<Block> blocks = new ArrayList<>();

		for (Field field : clazz.getFields()) {
			int mods = field.getModifiers();

			if (!Modifier.isPublic(mods)) continue;
			if (!Modifier.isStatic(mods)) continue;

			if (field.getType().equals(Block.class)) {
				try {
					blocks.add((Block) field.get(null));
				} catch (IllegalArgumentException | IllegalAccessException | ClassCastException e) {
					continue;
				}
			}
		}

		return blocks;
	}

	@Override
	public void generate() {
		for (Block block : getBlocks(RedBits.class)) {
			dropSelf(block);
		}
	}

}