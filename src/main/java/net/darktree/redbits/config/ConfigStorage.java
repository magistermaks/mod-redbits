package net.darktree.redbits.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.darktree.redbits.RedBits;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class ConfigStorage {

	private final static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private final Path path;

	public ConfigStorage(String name) {
		this.path = FabricLoader.getInstance().getConfigDir().resolve(name);
	}

	public <T> Optional<T> read(Class<T> clazz) {
		try {
			return Optional.ofNullable(GSON.fromJson(Files.readString(path), clazz));
		} catch (Exception exception) {
			return Optional.empty();
		}
	}

	public <T> void write(T config) {
		try {
			Files.writeString(path, GSON.toJson(config));
			RedBits.LOGGER.info("Config '{}' saved.", path);
		} catch (Exception exception) {
			RedBits.LOGGER.error("Unable to save config '{}'!", path);
		}
	}

}
