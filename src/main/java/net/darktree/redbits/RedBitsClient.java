package net.darktree.redbits;

import net.darktree.redbits.blocks.AnalogLampBlock;
import net.darktree.redbits.blocks.gate.EmitterBlock;
import net.darktree.redbits.utils.ColorProvider;
import net.darktree.redbits.utils.LookAtTickHandle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
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

		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> RedstoneWireBlock.getWireColor(1), RedBits.REDSTONE_EMITTER);
		ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> RedstoneWireBlock.getWireColor(state.get(EmitterBlock.POWER)), RedBits.REDSTONE_EMITTER);
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> ColorProvider.getColor(0), RedBits.RGB_LAMP);
		ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> ColorProvider.getColor(state.get(AnalogLampBlock.POWER)), RedBits.RGB_LAMP);

		// minecart renderer
		EntityRendererRegistry.register(RedBits.EMITTER_MINECART, ctx -> new MinecartEntityRenderer(ctx, EntityModelLayers.TNT_MINECART));

		ClientTickEvents.END_WORLD_TICK.register(world -> {
			PlayerEntity player = MinecraftClient.getInstance().player;

			if (player != null && !player.isSpectator()) {
				LookAtTickHandle.raytrace(player, client, point -> client = point);
			}
		});
	}

	@Environment(EnvType.CLIENT)
	private void cutout(Block block) {
		BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getCutout());
	}

}
