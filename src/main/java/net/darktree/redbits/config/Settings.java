package net.darktree.redbits.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "redbits")
public class Settings implements ConfigData {

	public int vision_distance = 128;
	public boolean disable_burnout = true;
	public boolean better_jukebox = true;

}