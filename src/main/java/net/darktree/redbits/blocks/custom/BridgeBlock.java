package net.darktree.redbits.blocks.custom;

import net.darktree.redbits.utils.TwoWayPower;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.ticks.TickPriority;

public class BridgeBlock extends CustomRedstoneGate {

	public static final EnumProperty<TwoWayPower> X_POWER = EnumProperty.create("x_power", TwoWayPower.class);
	public static final EnumProperty<TwoWayPower> Z_POWER = EnumProperty.create("z_power", TwoWayPower.class);

	private static final Direction[] HORIZONTAL = new Direction[] { Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST };
	private static final PowerConfig[] CONFIGS = new PowerConfig[] { new PowerConfig(X_POWER, Direction.Axis.X), new PowerConfig(Z_POWER, Direction.Axis.Z) };

	public BridgeBlock(Properties settings) {
		super(settings);
		this.registerDefaultState(this.stateDefinition.any().setValue(X_POWER, TwoWayPower.NONE).setValue(Z_POWER, TwoWayPower.NONE));
	}

	protected boolean hasPower(Level world, BlockPos pos, TwoWayPower x, TwoWayPower z) {
		return TwoWayPower.getPower(world, pos, this, x, Direction.Axis.X).hasPower() || TwoWayPower.getPower(world, pos, this, z, Direction.Axis.Z).hasPower();
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(X_POWER, Z_POWER);
	}

	private BlockState getNextState(Level world, BlockPos pos, BlockState state) {
		BlockState updated = state;

		for (PowerConfig config : CONFIGS) {
			TwoWayPower power = state.getValue(config.property);
			TwoWayPower.Unit next = TwoWayPower.getPower(world, pos, this, power, config.axis);
			boolean unlocked = power == TwoWayPower.NONE;

			if (!unlocked && next.hasPower()) {
				continue;
			}

			if (unlocked && next.hasPower()) {
				updated = updated.setValue(config.property, next.getDirection());
			}

			if (!next.hasPower()) {
				updated = updated.setValue(config.property, TwoWayPower.NONE);
			}
		}

		return updated;
	}

	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		BlockState updated = getNextState(world, pos, state);

		if (updated != state) {
			world.setBlock(pos, updated, Block.UPDATE_CLIENTS);
		}
	}

	@Override
	public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {

		if (direction.getAxis() == Direction.Axis.X && state.getValue(X_POWER).isAligned(direction)) {
			return 15;
		}

		if (direction.getAxis() == Direction.Axis.Z && state.getValue(Z_POWER).isAligned(direction)) {
			return 15;
		}

		return 0;
	}

	@Override
	protected void updatePowered(Level world, BlockPos pos, BlockState state) {
		BlockState next = getNextState(world, pos, state);

		if ((next != state) && !world.getBlockTicks().hasScheduledTick(pos, this)) {
			world.scheduleTick(pos, this, this.getUpdateDelayInternal(), TickPriority.HIGH);
		}
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		if (hasPower(world, pos, state.getValue(X_POWER), state.getValue(Z_POWER))) {
			world.scheduleTick(pos, this, 1);
		}
	}

	protected Direction[] getTargetDirections() {
		return HORIZONTAL;
	}

	@Override
	protected void updateTarget(Level world, BlockPos pos, BlockState state) {
		for (Direction direction : getTargetDirections()) {
			BlockPos target = pos.relative(direction);

			// does the same thing as TwoWayRepeater's updateTarget but for all four sides
			world.neighborChanged(target, this, null);
			world.updateNeighborsAtExceptFromFacing(target, this, direction.getOpposite(), null);
		}

		// needed so the gate won't get stuck when there is a switch-back
		world.neighborChanged(pos, this, null);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
		PowerConfig config = CONFIGS[random.nextInt(2)];
		TwoWayPower power = state.getValue(config.property);

		if (power != TwoWayPower.NONE) {
			CustomRedstoneGate.spawnSimpleParticles(DustParticleOptions.REDSTONE, world, pos, random, power.asDirection(config.axis), false, -5);
		}
	}

	static class PowerConfig {
		public final EnumProperty<TwoWayPower> property;
		public final Direction.Axis axis;

		public PowerConfig(EnumProperty<TwoWayPower> property, Direction.Axis axis) {
			this.property = property;
			this.axis = axis;
		}
	}

}
