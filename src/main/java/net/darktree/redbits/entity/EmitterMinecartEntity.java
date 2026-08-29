package net.darktree.redbits.entity;

import net.darktree.redbits.RedBits;
import net.darktree.redbits.blocks.EmitterBlock;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class EmitterMinecartEntity extends AbstractMinecart {

	private static final EntityDataAccessor<Integer> POWER = SynchedEntityData.defineId(EmitterMinecartEntity.class, EntityDataSerializers.INT);

	public EmitterMinecartEntity(EntityType<? extends EmitterMinecartEntity> type, Level world) {
		super(type, world);
	}

	@Override
	public BlockState getDefaultDisplayBlockState() {
		return RedBits.REDSTONE_EMITTER.defaultBlockState().setValue(EmitterBlock.POWER, getPower());
	}

	@Override
	public InteractionResult interact(Player player, InteractionHand hand) {
		this.cycle(player);
		return InteractionResult.SUCCESS;
	}

	@Override
	public ItemStack getPickResult() {
		return new ItemStack(RedBits.EMITTER_MINECART_ITEM);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(POWER, 1);
	}

	@Override
	protected void readAdditionalSaveData(ValueInput view) {
		super.readAdditionalSaveData(view);
		setPower(view.getIntOr("power", 1));
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput view) {
		super.addAdditionalSaveData(view);
		view.putInt("power", this.getPower());
	}

	private void cycle(Player player) {
		int power = EmitterBlock.interact(player, this.level(), this.blockPosition(), getPower());

		if (!this.level().isClientSide()) {
			setPower(power);
		}
	}

	public int getPower() {
		return this.entityData.get(POWER);
	}

	private void setPower(int power) {
		this.entityData.set(POWER, power);
	}

	@Override
	public Item getDropItem() {
		return RedBits.EMITTER_MINECART_ITEM;
	}

}
