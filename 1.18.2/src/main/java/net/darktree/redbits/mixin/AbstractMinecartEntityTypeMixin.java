package net.darktree.redbits.mixin;

import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.ArrayList;
import java.util.Arrays;

@Mixin(AbstractMinecartEntity.Type.class)
public abstract class AbstractMinecartEntityTypeMixin {

	@Invoker("<init>")
	private static AbstractMinecartEntity.Type init(String name, int id) {
		throw new AssertionError(); // unreachable statement
	}

	@Shadow
	@Final
	@Mutable
	private static AbstractMinecartEntity.Type[] field_7673;

	static {
		ArrayList<AbstractMinecartEntity.Type> values = new ArrayList<>(Arrays.asList(field_7673));
		AbstractMinecartEntity.Type last = values.get(values.size() - 1);

		// add new value
		values.add(init("EMITTER", last.ordinal() + 1));

		field_7673 = values.toArray(new AbstractMinecartEntity.Type[0]);
	}

}
