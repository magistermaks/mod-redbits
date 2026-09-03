package net.darktree.redbits.config;

import net.darktree.redbits.RedBits;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class RedBitsOptionsScreen extends OptionsSubScreen {

	private static final Component TEXT_YES = Component.translatable("gui.yes").setStyle(Style.EMPTY.withColor(0xff11ff11));
	private static final Component TEXT_NO = Component.translatable("gui.no").setStyle(Style.EMPTY.withColor(0xffff1111));

	private static OptionInstance<Boolean> create(String name, Supplier<Boolean> getter, Consumer<Boolean> setter, boolean requires_restart) {
		final MutableComponent description = Component.translatable("config.redbits.option." + name + ".tooltip");

		if (requires_restart) {
			description.append("\n\n").append(Component.translatable("config.redbits.requires_restart").setStyle(Style.EMPTY.withColor(0xffee1111)));
		}

		return new OptionInstance<>(
				"config.redbits.option." + name,
				OptionInstance.cachedConstantTooltip(description),
				(caption, value) -> value ? TEXT_YES : TEXT_NO,
				OptionInstance.BOOLEAN_VALUES,
				getter.get(),
				value -> {
					setter.accept(value);
					RedBits.CONFIG.save(); // write back to file
				}
		);
	}

	public static final OptionInstance<Boolean> DISABLE_BURNOUT = create(
			"disable_burnout",
			() -> RedBits.CONFIG.disable_burnout,
			value -> RedBits.CONFIG.disable_burnout = value,
			false
	);

	public static final OptionInstance<Boolean> JUKEBOX_INTEGRATION = create(
			"jukebox_integration",
			() -> RedBits.CONFIG.jukebox_integration,
			value -> RedBits.CONFIG.jukebox_integration = value,
			false
	);

	public static final OptionInstance<Boolean> CAMPFIRE_INTEGRATION = create(
			"campfire_integration",
			() -> RedBits.CONFIG.campfire_integration,
			value -> RedBits.CONFIG.campfire_integration = value,
			false
	);

	public static final OptionInstance<Boolean> ADD_GUIDE_TO_LOOT_TABLES = create(
			"add_guide_to_loot_tables",
			() -> RedBits.CONFIG.add_guide_to_loot_tables,
			value -> RedBits.CONFIG.add_guide_to_loot_tables = value,
			false
	);

	public static final OptionInstance<Boolean> ADD_GUIDE_TO_CREATIVE_MENU = create(
			"add_guide_to_creative_menu",
			() -> RedBits.CONFIG.add_guide_to_creative_menu,
			value -> RedBits.CONFIG.add_guide_to_creative_menu = value,
			true
	);

	public static List<OptionInstance<?>> getOptions() {
		return List.of(DISABLE_BURNOUT, JUKEBOX_INTEGRATION, CAMPFIRE_INTEGRATION, ADD_GUIDE_TO_LOOT_TABLES, ADD_GUIDE_TO_CREATIVE_MENU);
	}

	public RedBitsOptionsScreen(Screen previous) {
		super(previous, Minecraft.getInstance().options, Component.translatable("config.redbits.title"));
	}

	@Override
	protected void addOptions() {
		getOptions().forEach(list::addBig);
	}

}
