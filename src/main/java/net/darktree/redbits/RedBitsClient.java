package net.darktree.redbits;

import net.darktree.redbits.blocks.AnalogLampBlock;
import net.darktree.redbits.blocks.EmitterBlock;
import net.darktree.redbits.utils.ColorProvider;
import net.darktree.redbits.utils.LookAtTickHandle;
import net.darktree.redbits.utils.PatchouliProxy;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.entity.MinecartEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.player.PlayerEntity;

public class RedBitsClient implements ClientModInitializer {

	private static LookAtTickHandle.BlockPoint client = null;

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
		cutout(RedBits.JUNCTION);

//		FIXME ColorProviderRegistry.ITEM.register((stack, tintIndex) -> RedstoneWireBlock.getWireColor(1), RedBits.REDSTONE_EMITTER);
		ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> RedstoneWireBlock.getWireColor(state.get(EmitterBlock.POWER)), RedBits.REDSTONE_EMITTER);
//		FIXME ColorProviderRegistry.ITEM.register((stack, tintIndex) -> ColorProvider.getColor(0), RedBits.RGB_LAMP);
		ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> ColorProvider.getColor(state.get(AnalogLampBlock.POWER)), RedBits.RGB_LAMP);

		// minecart renderer
		EntityRendererRegistry.register(RedBits.EMITTER_MINECART, ctx -> new MinecartEntityRenderer(ctx, EntityModelLayers.TNT_MINECART));

		// nothing to see here
		// TODO
//		MessageInjector.inject("SSdtIHRoZSBtYW4gd2hvIGFycmFuZ2VzIHRoZSBibG9ja3Mh");
//		MessageInjector.inject("UGlyYWN5IGlzIGFsbCBhYm91dCBicmFuZGluZyE=");
//		MessageInjector.inject("QW5kIHdoYXQgY2FuIHlvdSBkbywgbXkgZWZmZW1pbmF0ZSBmZWxsb3c/");
//		MessageInjector.inject("Q2hlY2sgb3V0IFNlcXVlbnNhIFByb2dyYW1taW5nIExhbmd1YWdlIQ==");
//		MessageInjector.inject("WW91IGtub3cgdGhlIHJ1bGVzIGFuZCBzbyBkbyBJIQ==");
//		MessageInjector.inject("U3RhbmQgd2l0aCBVa3JhaW5lIQ==");
//		MessageInjector.inject("VGhlIG5vYmxlIGJlbmVmYWN0b3JzPyBHb25lLg==");
//		MessageInjector.inject("MDkgRjkh");
//		MessageInjector.inject("WW91IHdvbid0IGV2ZW4gZGllIGhvcnJpYmx5IQ==");
//		MessageInjector.inject("TW9zdCBWZXhpbmcgUGFyc2Uh");
//		MessageInjector.inject("QWxzbyBUcnkgU3BhY2UgU2hpZnRlciE=");
//
//		if (!PatchouliProxy.isModLoaded()) {
//			MessageInjector.inject("VHJ5IHdpdGggUGF0Y2hvdWxpIQ==");
//		}

		ClientTickEvents.END_WORLD_TICK.register(world -> {
			PlayerEntity player = MinecraftClient.getInstance().player;

			if (player != null && !player.isSpectator()) {
				LookAtTickHandle.raytrace(player, client, point -> client = point);
			}
		});
	}

	@Environment(EnvType.CLIENT)
	private void cutout(Block block) {
		BlockRenderLayerMap.putBlock(block, BlockRenderLayer.CUTOUT);
	}

}
