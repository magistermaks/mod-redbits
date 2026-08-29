package net.darktree.redbits.entity;

import net.darktree.redbits.RedBits;
import net.darktree.redbits.blocks.gate.EmitterBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class EmitterMinecartEntity extends AbstractMinecartEntity {

	private static final TrackedData<Integer> POWER = DataTracker.registerData(EmitterMinecartEntity.class, TrackedDataHandlerRegistry.INTEGER);

	public EmitterMinecartEntity(EntityType<? extends EmitterMinecartEntity> type, World world) {
		super(type, world);
	}

	@Override
	public BlockState getDefaultContainedBlock() {
		return RedBits.REDSTONE_EMITTER.getDefaultState().with(EmitterBlock.POWER, getPower());
	}

	@Override
	public ActionResult interact(PlayerEntity player, Hand hand) {
		this.cycle(player);
		return ActionResult.SUCCESS;
	}

	@Override
	public ItemStack getPickBlockStack() {
		return new ItemStack(RedBits.EMITTER_MINECART_ITEM);
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(POWER, 1);
	}

	@Override
	protected void readCustomData(ReadView view) {
		super.readCustomData(view);
		setPower(view.getInt("power", 1));
	}

	@Override
	protected void writeCustomData(WriteView view) {
		super.writeCustomData(view);
		view.putInt("power", this.getPower());
	}

	private void cycle(PlayerEntity player) {
		int power = EmitterBlock.interact(player, this.getWorld(), this.getBlockPos(), getPower());

		if (!this.getWorld().isClient()) {
			setPower(power);
		}
	}

	public int getPower() {
		return this.dataTracker.get(POWER);
	}

	private void setPower(int power) {
		this.dataTracker.set(POWER, power);
	}

	@Override
	public Item asItem() {
		return RedBits.EMITTER_MINECART_ITEM;
	}

}
