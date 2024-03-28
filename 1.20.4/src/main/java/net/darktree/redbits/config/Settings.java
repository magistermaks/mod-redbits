package net.darktree.redbits.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "redbits")
public class Settings implements ConfigData {

	public boolean disable_burnout = true;
	public boolean jukebox_integration = true;
	public boolean campfire_integration = true;

	@ConfigEntry.Gui.RequiresRestart
	public boolean add_guide_to_loot_tables = true;

	@ConfigEntry.Gui.RequiresRestart
	public boolean add_guide_to_creative_menu = true;

}