package net.darktree.redbits.config;

import com.google.gson.JsonElement;

public class Settings {

	private transient ConfigStorage storage;

	public boolean disable_burnout = true;
	public boolean jukebox_integration = true;
	public boolean campfire_integration = true;
	public boolean add_guide_to_loot_tables = true;
	public boolean add_guide_to_creative_menu = true;

	public static Settings readConfigFile(String name) {
		final ConfigStorage storage = new ConfigStorage(name);
		return storage.read(Settings.class).orElseGet(Settings::new).setStorage(storage);
	}

	public void save() {
		storage.write(this);
	}

	private Settings setStorage(ConfigStorage storage) {
		this.storage = storage;
		return this;
	}

}