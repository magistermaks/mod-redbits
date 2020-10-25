package net.darktree.redbits.utils;

import net.minecraft.state.property.IntProperty;

import java.util.Optional;

public class ColorProperty extends IntProperty {

    protected ColorProperty(String name) {
        super(name, 0, 15);
    }

    public static ColorProperty of( String name ) {
        return new ColorProperty( name );
    }

    public Optional<Integer> parse(String name) {
        int index = ColorProvider.fromColorName( name );

        if( index == -1 ) {
            return Optional.empty();
        }else{
            // no need to check for range as there always will be only 16 colors
            return Optional.of( index );
        }
    }

    public String name(Integer integer) {
        return ColorProvider.getColorName( integer );
    }

}
