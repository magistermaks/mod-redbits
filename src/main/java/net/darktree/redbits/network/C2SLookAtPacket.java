package net.darktree.redbits.network;

import net.darktree.redbits.RedBits;
import net.darktree.redbits.blocks.VisionSensorBlock;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class C2SLookAtPacket {

	public void register() {
		PayloadTypeRegistry.serverboundPlay().register(LookPayload.ID, LookPayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(LookPayload.ID, (payload, context) -> {
			apply(context.player(), payload.pos());
		});
	}

	private void apply(ServerPlayer player, BlockPos pos) {
		if (player != null && player.level() != null) {
			Level world = player.level();

			if (world.hasChunkAt(pos) && player.blockPosition().closerThan(pos, 130)) {
				VisionSensorBlock.trigger(world, pos);
				RedBits.LOOK_AT_SENSOR_CRITERION.trigger(player);
			}
		}
	}

	public void send(BlockPos pos) {
		ClientPlayNetworking.send(new LookPayload(pos));
	}

	record LookPayload(BlockPos pos) implements CustomPacketPayload {

		public static StreamCodec<FriendlyByteBuf, LookPayload> CODEC = CustomPacketPayload.codec(LookPayload::write, LookPayload::new);
		public static CustomPacketPayload.Type<LookPayload> ID = new Type<>(Identifier.fromNamespaceAndPath(RedBits.NAMESPACE, "look_at"));

		public LookPayload(FriendlyByteBuf packetByteBuf) {
			this(BlockPos.of(packetByteBuf.readLong()));
		}

		private void write(FriendlyByteBuf buf) {
			buf.writeLong(this.pos.asLong());
		}

		@Override
		public Type<LookPayload> type() {
			return ID;
		}

	}

}
