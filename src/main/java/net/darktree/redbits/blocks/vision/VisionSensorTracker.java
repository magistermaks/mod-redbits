package net.darktree.redbits.blocks.vision;

import net.darktree.redbits.RedBits;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class VisionSensorTracker {

    private static BlockPos target;

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register( VisionSensorTracker::apply );
    }

    private static void apply( MinecraftClient client ) {

        Entity entity = client.getCameraEntity();
        if( entity != null && client.world != null ) {

            HitResult hit = entity.raycast(RedBits.CONFIG.vision_distance, client.getTickDelta(), false);
            if( hit.getType() == HitResult.Type.BLOCK ) {

                BlockPos pos = ((BlockHitResult) hit).getBlockPos();
                BlockState state = client.world.getBlockState(pos);

                if( !pos.equals(target) ) {
                    if( state.getBlock() == RedBits.VISION_SENSOR ) {
                        VisionSensorNetwork.send( pos );
                    }
                    target = pos;
                }
            }
        }
    }

}
