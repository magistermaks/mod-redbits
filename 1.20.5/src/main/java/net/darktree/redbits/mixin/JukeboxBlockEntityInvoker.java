package net.darktree.redbits.mixin;

import net.minecraft.block.entity.JukeboxBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(JukeboxBlockEntity.class)
public interface JukeboxBlockEntityInvoker {

	@Invoker("startPlaying")
	void redbits_invokeStartPlaying();

	@Invoker("stopPlaying")
	void redbits_invokeStopPlaying();

}
