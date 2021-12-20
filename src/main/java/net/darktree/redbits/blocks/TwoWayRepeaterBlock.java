package net.darktree.redbits.blocks;

import net.darktree.redbits.utils.TwoWayPower;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;

import java.util.Random;

public class TwoWayRepeaterBlock extends AbstractRedstoneGate {

    public static final EnumProperty<TwoWayPower> POWER = EnumProperty.of( "power", TwoWayPower.class );
    public static final EnumProperty<Direction.Axis> AXIS = Properties.HORIZONTAL_AXIS;

    public TwoWayRepeaterBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(AXIS, Direction.Axis.X).with(POWER, TwoWayPower.NONE));
    }

    protected boolean hasPower(World world, BlockPos pos, BlockState state, TwoWayPower power ) {
        return this.getPower(world, pos, state, power).getPower() > 0;
    }

    protected TwoWayPowerUnit getPower(World world, BlockPos pos, BlockState state, TwoWayPower power ) {

        if (power == TwoWayPower.NONE) {
            TwoWayPowerUnit a = getPower( world, pos, state, TwoWayPower.FRONT );
            if ( a.getPower() > 0 ) return a;
            TwoWayPowerUnit b =  getPower( world, pos, state, TwoWayPower.BACK );
            if ( b.getPower() > 0 ) return b;
            return new TwoWayPowerUnit( TwoWayPower.NONE, 0 );
        }

        Direction direction = Direction.from( state.get(AXIS), power.asAxisDirection() );
        BlockPos blockPos = pos.offset( direction );

        return new TwoWayPowerUnit( power, getInputPower( world, blockPos, direction ) );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AXIS, POWER);
    }

    @Override
    public boolean connectsTo(BlockState state, Direction direction) {
        return state.get(AXIS) == direction.getAxis();
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        TwoWayPower power = state.get(POWER);
        TwoWayPowerUnit block = getPower(world, pos, state, power);
        boolean locked = power != TwoWayPower.NONE;

        if( !locked && block.getPower() > 0 && block.getDirection() != power ) {
            world.setBlockState(pos, state.with(POWER, block.getDirection()), 2);
        }else if( block.getPower() == 0 ) {
            world.setBlockState(pos, state.with(POWER, TwoWayPower.NONE), 2);
        }else if( !locked ) {
            world.setBlockState(pos, state.with(POWER, getPower(world, pos, state, TwoWayPower.NONE).getDirection()), 2);
            world.createAndScheduleBlockTick(pos, this, this.getUpdateDelayInternal(), TickPriority.VERY_HIGH);
        }
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if( state.get(POWER) == TwoWayPower.NONE ) {
            return 0;
        } else {
            return state.get(AXIS) == direction.getAxis() && state.get(POWER).isAligned( direction.getDirection() ) ? 15 : 0;
        }
    }

    @Override
    protected void updatePowered(World world, BlockPos pos, BlockState state) {
        boolean power = state.get(POWER) != TwoWayPower.NONE;
        boolean block = this.hasPower(world, pos, state, state.get(POWER));

        if (power != block && !world.getBlockTickScheduler().isTicking(pos, this)) {
            TickPriority tickPriority = TickPriority.HIGH;
            if (power) {
                tickPriority = TickPriority.VERY_HIGH;
            }

            world.createAndScheduleBlockTick(pos, this, this.getUpdateDelayInternal(), tickPriority);
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(AXIS, ctx.getPlayerFacing().getAxis());
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (this.hasPower(world, pos, state, state.get(POWER))) {
            world.createAndScheduleBlockTick(pos, this, 1);
        }
    }

    @Override
    protected void updateTarget(World world, BlockPos pos, BlockState state) {
        Direction direction = Direction.from( state.get(AXIS), Direction.AxisDirection.NEGATIVE ).getOpposite();
        BlockPos blockPos = pos.offset(direction);
        world.updateNeighbor(blockPos, this, pos);
        world.updateNeighborsExcept(blockPos, this, direction);

        direction = Direction.from( state.get(AXIS), Direction.AxisDirection.POSITIVE ).getOpposite();
        blockPos = pos.offset(direction);
        world.updateNeighbor(blockPos, this, pos);
        world.updateNeighborsExcept(blockPos, this, direction);
    }

    static class TwoWayPowerUnit {

        private final int power;
        private final TwoWayPower direction;

        TwoWayPowerUnit( TwoWayPower direction, int power ) {
            this.direction = direction;
            this.power = power;
        }

        public int getPower() {
            return power;
        }

        public TwoWayPower getDirection() {
            return direction;
        }

    }

}
