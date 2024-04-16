package net.darktree.redbits.network;

import io.netty.buffer.Unpooled;
import net.darktree.redbits.RedBits;
import net.darktree.redbits.blocks.VisionSensorBlock;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
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
		ServerSidePacketRegistry.INSTANCE.register(id, this::read);
	}

	public void read(PacketContext context, PacketByteBuf buffer) {
		BlockPos pos = buffer.readBlockPos();
		context.getTaskQueue().execute(() -> apply((ServerPlayerEntity) context.getPlayer(), pos));
	}

	private void apply(ServerPlayerEntity player, BlockPos pos) {
		if (player != null && player.getWorld() != null) {
			World world = player.getWorld();

			if (world.isChunkLoaded(pos) && player.getBlockPos().isWithinDistance(pos, 130)) {
				VisionSensorBlock.trigger(world, pos);
				RedBits.LOOK_AT_SENSOR_CRITERION.trigger(player);
			}
		}
	}

	public void send(BlockPos pos) {
		PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
		data.writeBlockPos(pos);
		ClientSidePacketRegistry.INSTANCE.sendToServer(id, data);
	}

}
