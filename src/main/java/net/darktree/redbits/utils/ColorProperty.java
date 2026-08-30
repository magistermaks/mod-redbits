package net.darktree.redbits.utils;

import java.util.Optional;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class ColorProperty extends IntegerProperty {

	protected ColorProperty(String name) {
		super(name, 0, ColorProvider.COUNT);
	}

	public static ColorProperty of(String name) {
		return new ColorProperty(name);
	}

	public Optional<Integer> getValue(String name) {
		int index = ColorProvider.fromColorName(name);

		if (index == -1) {
			return Optional.empty();
		} else {
			// no need to check for range as there always will be only `ColorProvider.COUNT` colors
			return Optional.of(index);
		}
	}

	public String getName(Integer integer) {
		return ColorProvider.getColorName(integer);
	}

}
