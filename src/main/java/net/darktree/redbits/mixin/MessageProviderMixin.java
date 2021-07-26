package net.darktree.redbits.mixin;

import net.minecraft.client.resource.SplashTextResourceSupplier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Base64;
import java.util.List;

@Mixin(SplashTextResourceSupplier.class)
abstract public class MessageProviderMixin {

    // Nothing to see here //

    @Final
    @Shadow
    private List<String> splashTexts;

    @Unique
    private void supply( String data ) {
        splashTexts.add( new String( Base64.getDecoder().decode(data) ) );
    }

    @Inject(at = @At("TAIL"), method = "apply(Ljava/util/List;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V")
    private void apply(List<String> list, ResourceManager resourceManager, Profiler profiler, CallbackInfo info) {
        supply("SSdtIHRoZSBtYW4gd2hvIGFycmFuZ2VzIHRoZSBibG9ja3Mh");
        supply("UGlyYWN5IGlzIGFsbCBhYm91dCBicmFuZGluZyE=");
        supply("QW5kIHdoYXQgY2FuIHlvdSBkbywgbXkgZWZmZW1pbmF0ZSBmZWxsb3c/");
        supply("Q2hlY2sgb3V0IFNlcXVlbnNhIFByb2dyYW1taW5nIExhbmd1YWdlIQ==");
        supply("WW91IGtub3cgdGhlIHJ1bGVzIGFuZCBzbyBkbyBJIQ==");
        supply("Q2hlY2sgb3V0IERhc2hMb2FkZXIh");
    }

}
