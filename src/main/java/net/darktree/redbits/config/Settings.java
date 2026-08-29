package net.darktree.redbits.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "redbits")
public class Settings implements ConfigData {

	@ConfigEntry.Gui.Tooltip(count=2)
	public boolean disable_burnout = true;

	@ConfigEntry.Gui.Tooltip(count=2)
	public boolean jukebox_integration = true;

	@ConfigEntry.Gui.Tooltip(count=2)
	public boolean campfire_integration = true;

	@ConfigEntry.Gui.RequiresRestart
	@ConfigEntry.Gui.Tooltip(count=2)
	public boolean add_guide_to_loot_tables = true;

	@ConfigEntry.Gui.RequiresRestart
	@ConfigEntry.Gui.Tooltip(count=2)
	public boolean add_guide_to_creative_menu = true;

}