package net.darktree.redbits.network;

import io.netty.buffer.Unpooled;
import net.darktree.redbits.blocks.VisionSensorBlock;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
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
		context.getTaskQueue().execute(() -> apply(context.getPlayer(), pos));
	}

	private void apply(PlayerEntity player, BlockPos pos) {
		if (player != null && player.world != null) {
			World world = player.world;

			if (world.isChunkLoaded(pos) && player.getBlockPos().isWithinDistance(pos, 130)) {
				VisionSensorBlock.trigger(world, pos);
			}
		}
	}

	public void send(BlockPos pos) {
		PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
		data.writeBlockPos(pos);
		ClientSidePacketRegistry.INSTANCE.sendToServer(id, data);
	}

}
