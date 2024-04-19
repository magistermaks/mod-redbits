package net.darktree.redbits.utils;

import net.darktree.redbits.RedBits;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.lang.reflect.Method;

public class PatchouliProxy {

	private static final String PATCHOULI_ID = "patchouli";
	private static final String PATCHOULI_API = "vazkii.patchouli.api.PatchouliAPI";
	private static final String PATCHOULI_SOUNDS = "vazkii.patchouli.common.base.PatchouliSounds";
	private static final String PATCHOULI_INTERFACE = "vazkii.patchouli.api.PatchouliAPI$IPatchouliAPI";

	private final Object api;
	private final Object sound;
	private final Method method;

	private PatchouliProxy(Object api, Object sound, Method method) {
		this.api = api;
		this.sound = sound;
		this.method = method;
	}

	public static PatchouliProxy create() {
		if (!isModLoaded()) {
			return null;
		}

		try {
			Object api = Class.forName(PATCHOULI_API).getMethod("get").invoke(null);
			Object sound = Class.forName(PATCHOULI_SOUNDS).getField("BOOK_OPEN").get(null);
			Method method = Class.forName(PATCHOULI_INTERFACE).getMethod("openBookGUI", ServerPlayerEntity.class, Identifier.class);
			Method check = Class.forName(PATCHOULI_INTERFACE).getMethod("isStub");

			if ((Boolean) check.invoke(api)) {
				return null;
			}

			return new PatchouliProxy(api, sound, method);
		} catch (Throwable throwable) {
			RedBits.LOGGER.error("Failed to acquire Patchouli API access!", throwable);
			return null;
		}
	}

	public void openBook(ServerPlayerEntity player, Identifier book) {
		try {
			method.invoke(api, player, book);
			player.playSound((SoundEvent) sound, 1f, (float) (0.7 + Math.random() * 0.4));
		} catch (Throwable throwable) {
			RedBits.LOGGER.error("Failed to open book on the acquired API instance!", throwable);
		}
	}

	public static boolean isModLoaded() {
		return FabricLoader.getInstance().isModLoaded(PATCHOULI_ID);
	}

}
