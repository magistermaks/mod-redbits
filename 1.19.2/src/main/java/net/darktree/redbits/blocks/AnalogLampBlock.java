package net.darktree.redbits.blocks;

import net.darktree.redbits.utils.ColorProperty;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class AnalogLampBlock extends Block {
    public static final ColorProperty POWER = ColorProperty.of("color");

    public AnalogLampBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(POWER, 0));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(POWER, ctx.getWorld().getReceivedRedstonePower(ctx.getBlockPos()));
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        if (!world.isClient) {
            int power = world.getReceivedRedstonePower(pos);

            if (state.get(POWER) != power) {
                world.setBlockState(pos, state.with(POWER, power), 2);
            }
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int power = world.getReceivedRedstonePower(pos);

        if (state.get(POWER) != power) {
            world.setBlockState(pos, state.with(POWER, power), 2);
        }

    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWER);
    }

}
