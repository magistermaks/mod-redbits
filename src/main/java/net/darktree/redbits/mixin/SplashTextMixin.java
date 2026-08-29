package net.darktree.redbits.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.darktree.redbits.utils.PatchouliProxy;
import net.minecraft.client.resource.SplashTextResourceSupplier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.Text;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.function.Consumer;

@Mixin(SplashTextResourceSupplier.class)
public class SplashTextMixin {

	@WrapMethod(method = "prepare(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)Ljava/util/List;")
	protected List<Text> prepare(ResourceManager resourceManager, Profiler profiler, Operation<List<Text>> original) {
		List<Text> injected = new ArrayList<>(original.call(resourceManager, profiler));

		Consumer<String> inject = encoded -> {
			injected.add(Text.literal(new String(Base64.getDecoder().decode(encoded))));
		};

		// nothing to see here
		inject.accept("SSdtIHRoZSBtYW4gd2hvIGFycmFuZ2VzIHRoZSBibG9ja3Mh");
		inject.accept("UGlyYWN5IGlzIGFsbCBhYm91dCBicmFuZGluZyE=");
		inject.accept("QW5kIHdoYXQgY2FuIHlvdSBkbywgbXkgZWZmZW1pbmF0ZSBmZWxsb3c/");
		inject.accept("Q2hlY2sgb3V0IFNlcXVlbnNhIFByb2dyYW1taW5nIExhbmd1YWdlIQ==");
		inject.accept("WW91IGtub3cgdGhlIHJ1bGVzIGFuZCBzbyBkbyBJIQ==");
		inject.accept("U3RhbmQgd2l0aCBVa3JhaW5lIQ==");
		inject.accept("VGhlIG5vYmxlIGJlbmVmYWN0b3JzPyBHb25lLg==");
		inject.accept("MDkgRjkh");
		inject.accept("WW91IHdvbid0IGV2ZW4gZGllIGhvcnJpYmx5IQ==");
		inject.accept("TW9zdCBWZXhpbmcgUGFyc2Uh");
		inject.accept("QWxzbyBUcnkgU3BhY2UgU2hpZnRlciE=");
		inject.accept("QWxzbyBUcnkgTG9vcHkgR29lcyBGaXNoaW5nIQ==");
		inject.accept("VmlyaWRlc2NlbnQh");
		inject.accept("VGhlIHdvcmxkIGlzIGEgZ2FtZSwgYnkgYSBtYWtlciBpbnNhbmUu");
		inject.accept("SW5uYXRlIGJlYXV0eSBvZiB0aG91Z2h0IGV4cHJlc3NlZCE=");
		inject.accept("Q292ZXJlZCBieSBhc3BoYWx0LCBidXJyaWVkIGluIHN0b25lLg==");
		inject.accept("U28gcGVyZmVjdGx5IHdvdmVuIQ==");
		inject.accept("TGFuZCBjb2F0ZWQgaW4gcmVncmV0cyE=");
		inject.accept("RGV2b2lkIG9mIGxpZmUu");

		if (!PatchouliProxy.isModLoaded()) {
			inject.accept("VHJ5IHdpdGggUGF0Y2hvdWxpIQ==");
		}

		return List.copyOf(injected);
	}



}
