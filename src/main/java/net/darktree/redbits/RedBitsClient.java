package net.darktree.redbits;

import net.darktree.redbits.blocks.AnalogLampBlock;
import net.darktree.redbits.blocks.EmitterBlock;
import net.darktree.redbits.utils.ColorProvider;
import net.darktree.redbits.utils.LookAtTickHandle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedStoneWireBlock;

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

		ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> RedStoneWireBlock.getColorForPower(state.getValue(EmitterBlock.POWER)), RedBits.REDSTONE_EMITTER);
		ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> ColorProvider.getColor(state.getValue(AnalogLampBlock.POWER)), RedBits.RGB_LAMP);

		// minecart renderer
		EntityRendererRegistry.register(RedBits.EMITTER_MINECART, ctx -> new MinecartRenderer(ctx, ModelLayers.TNT_MINECART));

		ClientTickEvents.END_WORLD_TICK.register(world -> {
			Player player = Minecraft.getInstance().player;

			if (player != null && !player.isSpectator()) {
				LookAtTickHandle.raytrace(player, client, point -> client = point);
			}
		});
	}

	@Environment(EnvType.CLIENT)
	private void cutout(Block block) {
		BlockRenderLayerMap.putBlock(block, ChunkSectionLayer.CUTOUT);
	}

}
