package net.darktree.redbits.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;

@Config(name = "redbits")
public class RedBitsConfig extends PartitioningSerializer.GlobalData {

	@ConfigEntry.Category("gates")
	@ConfigEntry.Gui.TransitiveObject
	public GatesCategory gates = new GatesCategory();

	@ConfigEntry.Category("pressure_plates")
	@ConfigEntry.Gui.TransitiveObject
	public PressurePlatesCategory pressure_plates = new PressurePlatesCategory();

	@ConfigEntry.Category("other")
	@ConfigEntry.Gui.TransitiveObject
	public OtherCategory other = new OtherCategory();

	@Config(name = "gates")
	public static class GatesCategory implements ConfigData {

		@ConfigEntry.Gui.RequiresRestart
		public boolean inverter = true;

		@ConfigEntry.Gui.RequiresRestart
		public boolean t_flip_flop = true;

		@ConfigEntry.Gui.RequiresRestart
		public boolean detector = true;

		@ConfigEntry.Gui.RequiresRestart
		public boolean two_way_repeater = true;

		@ConfigEntry.Gui.RequiresRestart
		public boolean latch = true;

		@ConfigEntry.Gui.RequiresRestart
		public boolean timer = true;

	}

	@Config(name = "pressure_plates")
	public static class PressurePlatesCategory implements ConfigData {

		@ConfigEntry.Gui.RequiresRestart
		public boolean obsidian = true;

		@ConfigEntry.Gui.RequiresRestart
		public boolean crying_obsidian = true;

		@ConfigEntry.Gui.RequiresRestart
		public boolean end_stone = true;

		@ConfigEntry.Gui.RequiresRestart
		public boolean basalt = true;

	}

	@Config(name = "other")
	public static class OtherCategory implements ConfigData {

		public int vision_distance = 128;

		@ConfigEntry.Gui.RequiresRestart
		public boolean vision_sensor = true;

		@ConfigEntry.Gui.RequiresRestart
		public boolean large_button = true;

		@ConfigEntry.Gui.RequiresRestart
		public boolean shaded_redstone_lamp = true;

		@ConfigEntry.Gui.RequiresRestart
		public boolean rgb_lamp = true;

		@ConfigEntry.Gui.RequiresRestart
		public boolean redstone_emitter = true;

		@ConfigEntry.Gui.RequiresRestart
		public boolean inverted_redstone_torch = true;

	}
}