package net.darktree.redbits.datagen;

import net.darktree.redbits.RedBits;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.world.level.block.Block;

public class RedBitsModelProvider extends FabricModelProvider {

	public RedBitsModelProvider(FabricPackOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(BlockModelGenerators generators) {

	}

	@Override
	public void generateItemModels(ItemModelGenerators generators) {
		Block[] blocks = {
				RedBits.BRIDGE,
				RedBits.DETECTOR,
				RedBits.INVERTER,
				RedBits.JUNCTION,
				RedBits.LATCH,
				RedBits.PROJECTOR,
				RedBits.T_FLIP_FLOP,
				RedBits.TIMER,
				RedBits.TWO_WAY_REPEATER
		};

		for (Block block : blocks) {
			generators.generateFlatItem(block.asItem(), ModelTemplates.FLAT_ITEM);
		}

		generators.generateFlatItem(RedBits.EMITTER_MINECART_ITEM, ModelTemplates.FLAT_ITEM);
		generators.generateFlatItem(RedBits.GUIDE, ModelTemplates.FLAT_ITEM);
		generators.generateFlatItem(RedBits.INVERTED_REDSTONE_TORCH.asItem(), RedBits.id("block/redstone_torch_off"), ModelTemplates.FLAT_ITEM);
	}

	@Override
	public String getName() {
		return "RedBitsModelProvider";
	}

}
