package net.darktree.redbits;

import net.darktree.redbits.datagen.RedBitsLootTableProvider;
import net.darktree.redbits.datagen.RedBitsModelProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class RedBitsData implements DataGeneratorEntrypoint {

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		FabricDataGenerator.Pack pack = generator.createPack();

		pack.addProvider(RedBitsLootTableProvider::new);
		pack.addProvider(RedBitsModelProvider::new);
	}

}