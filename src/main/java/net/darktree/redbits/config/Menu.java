package net.darktree.redbits.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfigClient;
import net.minecraft.client.gui.screen.Screen;

public class Menu implements ModMenuApi {

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> (Screen) AutoConfigClient.getConfigScreen(Settings.class, parent).get();
	}

}
