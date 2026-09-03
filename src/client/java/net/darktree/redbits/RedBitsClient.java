package net.darktree.redbits;

import net.darktree.redbits.blocks.AnalogLampBlock;
import net.darktree.redbits.blocks.EmitterBlock;
import net.darktree.redbits.utils.ColorProvider;
import net.darktree.redbits.utils.LookAtTickHandle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.RedstoneWireBlock;

import java.util.List;

public class RedBitsClient implements ClientModInitializer {

	private static LookAtTickHandle.BlockPoint client = null;

	@Override
	public void onInitializeClient() {
		BlockColorRegistry.register(List.of(state -> RedstoneWireBlock.getColorForPower(state.getValue(EmitterBlock.POWER))), RedBits.REDSTONE_EMITTER);
		BlockColorRegistry.register(List.of(state -> ColorProvider.getColor(state.getValue(AnalogLampBlock.POWER))), RedBits.RGB_LAMP);

		// minecart renderer
		EntityRendererRegistry.register(RedBits.EMITTER_MINECART, ctx -> new MinecartRenderer(ctx, ModelLayers.TNT_MINECART));

		ClientTickEvents.END_LEVEL_TICK.register(world -> {
			Player player = Minecraft.getInstance().player;

			if (player != null && !player.isSpectator()) {
				LookAtTickHandle.raytrace(player, client, point -> client = point);
			}
		});
	}

}
