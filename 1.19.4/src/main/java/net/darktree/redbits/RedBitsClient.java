package net.darktree.redbits;

import net.darktree.interference.MessageInjector;
import net.darktree.redbits.blocks.AnalogLampBlock;
import net.darktree.redbits.blocks.EmitterBlock;
import net.darktree.redbits.utils.ColorProvider;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.MinecartEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;

public class RedBitsClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		cutout(RedBits.INVERTER);
		cutout(RedBits.T_FLIP_FLOP);
		cutout(RedBits.DETECTOR);
		cutout(RedBits.LATCH);
		cutout(RedBits.TWO_WAY_REPEATER);
		cutout(RedBits.INVERTED_REDSTONE_TORCH);
		cutout(RedBits.INVERTED_REDSTONE_WALL_TORCH);
		cutout(RedBits.TIMER);
		cutout(RedBits.BRIDGE);
		cutout(RedBits.PROJECTOR);

		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> RedstoneWireBlock.getWireColor(1), RedBits.REDSTONE_EMITTER);
		ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> RedstoneWireBlock.getWireColor(state.get(EmitterBlock.POWER)), RedBits.REDSTONE_EMITTER);
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> ColorProvider.getColor(0), RedBits.RGB_LAMP);
		ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> ColorProvider.getColor(state.get(AnalogLampBlock.POWER)), RedBits.RGB_LAMP);

		// minecart renderer
		EntityRendererRegistry.register(RedBits.EMITTER_MINECART, ctx -> new MinecartEntityRenderer<>(ctx, EntityModelLayers.TNT_MINECART));

		// nothing to see here
		MessageInjector.inject("SSdtIHRoZSBtYW4gd2hvIGFycmFuZ2VzIHRoZSBibG9ja3Mh");
		MessageInjector.inject("UGlyYWN5IGlzIGFsbCBhYm91dCBicmFuZGluZyE=");
		MessageInjector.inject("QW5kIHdoYXQgY2FuIHlvdSBkbywgbXkgZWZmZW1pbmF0ZSBmZWxsb3c/");
		MessageInjector.inject("Q2hlY2sgb3V0IFNlcXVlbnNhIFByb2dyYW1taW5nIExhbmd1YWdlIQ==");
		MessageInjector.inject("WW91IGtub3cgdGhlIHJ1bGVzIGFuZCBzbyBkbyBJIQ==");
		MessageInjector.inject("Q2hlY2sgb3V0IERhc2hMb2FkZXIh");
		MessageInjector.inject("VHJ5IHdpdGggUGF0Y2hvdWxpIQ==");
		MessageInjector.inject("U3RhbmQgd2l0aCBVa3JhaW5lIQ==");
		MessageInjector.inject("VGhlIG5vYmxlIGJlbmVmYWN0b3JzPyBHb25lLg==");
	}

	@Environment(EnvType.CLIENT)
	private void cutout(Block block) {
		BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getCutout());
	}

}
