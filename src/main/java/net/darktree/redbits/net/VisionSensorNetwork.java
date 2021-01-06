package net.darktree.redbits.net;

import io.netty.buffer.Unpooled;
import net.darktree.redbits.RedBits;
import net.darktree.redbits.blocks.VisionSensorBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class VisionSensorNetwork {

    public static final Identifier VISION_SENSOR_ACTIVATE = new Identifier("redbits", "vision_sensor_activate");

    public static void init() {
        ServerSidePacketRegistry.INSTANCE.register(VISION_SENSOR_ACTIVATE, (ctx, data) -> {
            BlockPos pos = data.readBlockPos();
            ctx.getTaskQueue().execute(() -> apply( ctx.getPlayer(), pos ));
        });
    }

    private static void apply(PlayerEntity player, BlockPos pos) {
        if( player != null && player.world != null && player.world.canSetBlock(pos) ) {

            BlockState state = player.world.getBlockState(pos);
            if( state.getBlock() == RedBits.VISION_SENSOR ) {
                if( ((VisionSensorBlock) RedBits.VISION_SENSOR).activate(state, player.world, pos) ) {
                    return;
                }
            }

            RedBits.LOGGER.warn( "Invalid Vision Sensor activation packet, sent by: " + player.getNameAndUuid() + "!" );

        }
    }

    @Environment(EnvType.CLIENT)
    public static void send( BlockPos pos ) {
        PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
        data.writeBlockPos( pos );
        ClientSidePacketRegistry.INSTANCE.sendToServer( VISION_SENSOR_ACTIVATE, data );
    }


}
