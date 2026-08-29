package net.darktree.redbits.network;

import net.darktree.redbits.RedBits;
import net.darktree.redbits.blocks.VisionSensorBlock;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class C2SLookAtPacket {

	public void register() {
		PayloadTypeRegistry.playC2S().register(LookPayload.ID, LookPayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(LookPayload.ID, (payload, context) -> {
			apply(context.player(), payload.pos());
		});
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
		ClientPlayNetworking.send(new LookPayload(pos));
	}

	record LookPayload(BlockPos pos) implements CustomPayload {

		public static PacketCodec<PacketByteBuf, LookPayload> CODEC = CustomPayload.codecOf(LookPayload::write, LookPayload::new);
		public static CustomPayload.Id<LookPayload> ID = new Id<>(Identifier.of(RedBits.NAMESPACE, "look_at"));

		public LookPayload(PacketByteBuf packetByteBuf) {
			this(BlockPos.fromLong(packetByteBuf.readLong()));
		}

		private void write(PacketByteBuf buf) {
			buf.writeLong(this.pos.asLong());
		}

		@Override
		public Id<LookPayload> getId() {
			return ID;
		}

	}

}
