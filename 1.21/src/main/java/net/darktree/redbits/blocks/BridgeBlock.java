package net.darktree.redbits.blocks;

import net.darktree.redbits.utils.TwoWayPower;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.tick.TickPriority;

@SuppressWarnings("deprecation")
public class BridgeBlock extends AbstractRedstoneGate {

	public static final EnumProperty<TwoWayPower> X_POWER = EnumProperty.of("x_power", TwoWayPower.class);
	public static final EnumProperty<TwoWayPower> Z_POWER = EnumProperty.of("z_power", TwoWayPower.class);

	private static final Direction[] HORIZONTAL = new Direction[] { Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST };
	private static final PowerConfig[] CONFIGS = new PowerConfig[] { new PowerConfig(X_POWER, Direction.Axis.X), new PowerConfig(Z_POWER, Direction.Axis.Z) };

	public BridgeBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(X_POWER, TwoWayPower.NONE).with(Z_POWER, TwoWayPower.NONE));
	}

	protected boolean hasPower(World world, BlockPos pos, TwoWayPower x, TwoWayPower z) {
		return TwoWayPower.getPower(world, pos, this, x, Direction.Axis.X).hasPower() || TwoWayPower.getPower(world, pos, this, z, Direction.Axis.Z).hasPower();
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(X_POWER, Z_POWER);
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		BlockState newState = state;

		for (PowerConfig config : CONFIGS) {
			TwoWayPower power = state.get(config.property);
			TwoWayPower.Unit next = TwoWayPower.getPower(world, pos, this, power, config.axis);
			boolean unlocked = power == TwoWayPower.NONE;

			if (!unlocked && next.hasPower()) {
				continue;
			}

			if (unlocked && next.hasPower()) {
				newState = newState.with(config.property, next.getDirection());
			}

			if (!next.hasPower()) {
				newState = newState.with(config.property, TwoWayPower.NONE);
			}
		}

		if (newState != state) {
			world.setBlockState(pos, newState, 2);
		}
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {

		if (direction.getAxis() == Direction.Axis.X && state.get(X_POWER).isAligned(direction)) {
			return 15;
		}

		if (direction.getAxis() == Direction.Axis.Z && state.get(Z_POWER).isAligned(direction)) {
			return 15;
		}

		return 0;
	}

	@Override
	protected void updatePowered(World world, BlockPos pos, BlockState state) {
		if (!world.getBlockTickScheduler().isQueued(pos, this)) {
			world.scheduleBlockTick(pos, this, this.getUpdateDelayInternal(), TickPriority.HIGH);
		}
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		if (hasPower(world, pos, state.get(X_POWER), state.get(Z_POWER))) {
			world.scheduleBlockTick(pos, this, 1);
		}
	}

	protected Direction[] getTargetDirections() {
		return HORIZONTAL;
	}

	@Override
	protected void updateTarget(World world, BlockPos pos, BlockState state) {
		for (Direction direction : getTargetDirections()) {
			BlockPos target = pos.offset(direction);

			// does the same thing as TwoWayRepeater's updateTarget but for all four sides
			world.updateNeighbor(target, this, pos);
			world.updateNeighborsExcept(target, this, direction.getOpposite());
		}

		// needed so the gate won't get stuck when there is a switch-back
		world.updateNeighbor(pos, this, pos);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		PowerConfig config = CONFIGS[random.nextInt(2)];
		TwoWayPower power = state.get(config.property);

		if (power != TwoWayPower.NONE) {
			AbstractRedstoneGate.spawnSimpleParticles(DustParticleEffect.DEFAULT, world, pos, random, power.asDirection(config.axis), false, -5);
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
