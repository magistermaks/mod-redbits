package net.darktree.redbits.blocks;

import net.darktree.redbits.utils.FacingDirection;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;

import java.util.Random;

public class LatchBlock extends AbstractRedstoneGate {

    public static final EnumProperty<FacingDirection> POWER = EnumProperty.of( "power", FacingDirection.class );
    public static final EnumProperty<Direction.Axis> AXIS = Properties.HORIZONTAL_AXIS;

    public LatchBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(AXIS, Direction.Axis.X).with(POWER, FacingDirection.FRONT));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AXIS, POWER);
    }

    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(AXIS) == direction.getAxis() && state.get(POWER).asAxisDirection() == direction.getOpposite().getDirection() ? 15 : 0;
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(AXIS, ctx.getPlayerFacing().getAxis()).with(POWER, FacingDirection.from( ctx.getPlayerFacing().getDirection() ) );
    }

    @Override
    public boolean connectsTo(BlockState state, Direction direction) {
        return state.get(AXIS) == direction.getAxis();
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (this.hasPower(world, pos, state, state.get(POWER).other())) {
            world.getBlockTickScheduler().schedule(pos, this, 1);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if( player == null || player.abilities.allowModifyWorld ) {
            world.setBlockState( pos, state.with( POWER, state.get(POWER).other()) );
            world.playSound( null, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 1.0f, 0.7f );
            this.updatePowered( world, pos, state );
            return ActionResult.SUCCESS;
        }
        return super.onUse( state, world, pos, player, hand, hit );
    }

    protected void updatePowered(World world, BlockPos pos, BlockState state) {
        boolean block = this.hasPower(world, pos, state, state.get(POWER).other());

        if( block && !world.getBlockTickScheduler().isTicking(pos, this) ) {
            TickPriority tickPriority = TickPriority.HIGH;
            if (this.isTargetNotAligned(world, pos, state)) {
                tickPriority = TickPriority.EXTREMELY_HIGH;
            }
            world.getBlockTickScheduler().schedule(pos, this, this.getUpdateDelayInternal(), tickPriority);
        }
    }

    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        FacingDirection power = state.get(POWER).other();

        if( getPower(world, pos, state, power) > 0 ) {
            world.setBlockState(pos, state.with(POWER, power), 2);
        }
    }

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

    protected boolean hasPower(World world, BlockPos pos, BlockState state, FacingDirection power ) {
        return this.getPower(world, pos, state, power) > 0;
    }

    protected int getPower(World world, BlockPos pos, BlockState state, FacingDirection power ) {
        Direction direction = Direction.from( state.get(AXIS), power.asAxisDirection() );
        BlockPos blockPos = pos.offset( direction );
        return getInputPower( world, blockPos, direction );
    }

    public boolean isTargetNotAligned(BlockView world, BlockPos pos, BlockState state) {
        Direction direction = Direction.from( state.get(AXIS), state.get(POWER).asAxisDirection() );
        BlockState blockState = world.getBlockState(pos.offset(direction));
        return isRedstoneGate(blockState) && blockState.get(HorizontalFacingBlock.FACING) != direction;
    }

}
