package net.darktree.redbits.entity;

import net.darktree.redbits.RedBits;
import net.darktree.redbits.blocks.EmitterBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class EmitterMinecartEntity extends AbstractMinecartEntity {

	private static final TrackedData<Integer> POWER = DataTracker.registerData(EmitterMinecartEntity.class, TrackedDataHandlerRegistry.INTEGER);
	public static AbstractMinecartEntity.Type EMITTER = AbstractMinecartEntity.Type.valueOf("EMITTER");

	public EmitterMinecartEntity(EntityType<Entity> entity, World world) {
		super(entity, world);
	}

	public EmitterMinecartEntity(World world, double x, double y, double z) {
		super(RedBits.EMITTER_MINECART, world, x, y, z);
	}

	@Override
	protected void applySlowdown() {
		float f = 0.98f + 15 * 0.001f;
		if (this.isTouchingWater()) {
			f *= 0.95f;
		}
		this.setVelocity(this.getVelocity().multiply(f, 0.0, f));
	}

	@Override
	public AbstractMinecartEntity.Type getMinecartType() {
		return EMITTER;
	}

	@Override
	public BlockState getDefaultContainedBlock() {
		return RedBits.REDSTONE_EMITTER.getDefaultState().with(EmitterBlock.POWER, getPower());
	}

	@Override
	public void dropItems(DamageSource source) {
		super.dropItems(source);
		if (!source.isExplosive() && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
			this.dropItem(RedBits.REDSTONE_EMITTER);
		}
	}

	@Override
	public ActionResult interact(PlayerEntity player, Hand hand) {
		this.cycle(player);
		return ActionResult.success(this.world.isClient);
	}

	@Override
	public ItemStack getPickBlockStack() {
		return new ItemStack(RedBits.EMITTER_MINECART_ITEM);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(POWER, 1);
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("power", NbtElement.NUMBER_TYPE)) {
			setPower(nbt.getInt("power"));
		}
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("power", getPower());
	}

	private void cycle(PlayerEntity player) {
		int power = EmitterBlock.interact(player, this.world, this.getBlockPos(), getPower());

		if (!this.world.isClient) {
			setPower(power);
		}
	}

	public int getPower() {
		return this.dataTracker.get(POWER);
	}

	public void setPower(int power) {
		this.dataTracker.set(POWER, power);
	}

}
