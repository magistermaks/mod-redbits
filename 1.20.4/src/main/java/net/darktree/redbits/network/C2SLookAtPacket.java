package net.darktree.redbits.network;

import io.netty.buffer.Unpooled;
import net.darktree.redbits.blocks.VisionSensorBlock;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class C2SLookAtPacket {

	private final Identifier id;

	public C2SLookAtPacket(Identifier id) {
		this.id = id;
	}

	public void register() {
		ServerPlayNetworking.registerGlobalReceiver(id, this::read);
	}

	public void read(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buffer, PacketSender sender) {
		BlockPos pos = buffer.readBlockPos();
		server.execute(() -> apply(player, pos));
	}

	private void apply(PlayerEntity player, BlockPos pos) {
		if (player != null && player.getWorld() != null) {
			World world = player.getWorld();

			if (world.isChunkLoaded(pos) && player.getBlockPos().isWithinDistance(pos, 130)) {
				VisionSensorBlock.trigger(world, pos);
			}
		}
	}

	public void send(BlockPos pos) {
		PacketByteBuf data = PacketByteBufs.create();
		data.writeBlockPos(pos);
		ClientPlayNetworking.send(id, data);
	}

}
