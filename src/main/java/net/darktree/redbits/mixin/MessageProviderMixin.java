package net.darktree.redbits.mixin;

import net.minecraft.client.resource.SplashTextResourceSupplier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SplashTextResourceSupplier.class)
abstract public class MessageProviderMixin {

    // Nothing to see here //

    @Final
    @Shadow
    private List<String> splashTexts;

    @Inject(at = @At("TAIL"), method = "apply(Ljava/util/List;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V")
    private void apply(List<String> list, ResourceManager resourceManager, Profiler profiler, CallbackInfo info) {
        splashTexts.add("I" + "'m the man who arranges the blocks!");
        splashTexts.add("P" + "iracy is all about branding!");
        splashTexts.add("A" + "nd what can you do, my effeminate fellow?");
        splashTexts.add("C" + "heck out Sequensa Programming Language!");
        splashTexts.add("Y" + "ou know the rules and so do I!");
    }

}
