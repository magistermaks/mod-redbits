package net.darktree.redbits.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "redbits")
public class Settings implements ConfigData {

	public int vision_distance = 128;

}