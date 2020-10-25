package net.darktree.redbits.utils;

import java.util.ArrayList;
import java.util.Arrays;

public class ColorProvider {

    private static final int BASE = 0x33;

    private static final int[] COLORS = {
            make( 0x1D1D21 ), // black dye
            make( 0xB02E26 ), // red dye
            make( 0x5E7C16 ), // green dye
            make( 0x835432 ), // brown dye
            make( 0x3C44AA ), // blue dye
            make( 0x8932B8 ), // purple dye
            make( 0x169C9C ), // cyan dye
            make( 0x9D9D97 ), // light gray dye
            make( 0x474F52 ), // gray dye
            make( 0xF38BAA ), // pink dye
            make( 0x80C71F ), // lime dye
            make( 0xFED83D ), // yellow dye
            make( 0x3AB3DA ), // light blue dye
            make( 0xC74EBD ), // magenta dye
            make( 0xF9801D ), // orange dye
            make( 0xF9FFFE ), // white dye
    };

    private static final ArrayList<String> NAMES = new ArrayList<>( Arrays.asList(
            "black",
            "red",
            "green",
            "brown",
            "blue",
            "purple",
            "cyan",
            "light_gray",
            "gray",
            "pink",
            "lime",
            "yellow",
            "light_blue",
            "magenta",
            "orange",
            "white"
    ));

    private static int make( int baseColor ) {
        int r = Math.min( ((baseColor >> 16) & 0xFF) + BASE, 0xFF );
        int g = Math.min( ((baseColor >> 8) & 0xFF) + BASE, 0xFF );
        int b = Math.min( (baseColor & 0xFF) + BASE, 0xFF );

        return (r << 16) + (g << 8) + b;
    }

    public static int getColor( int index ) {
        if( index < 0 ) return 0;
        if( index > 15 ) return 0;

        return COLORS[index];
    }

    public static String getColorName( int index ) {
        if( index < 0 ) return "invalid";
        if( index > 15 ) return "invalid";

        return NAMES.get(index);
    }

    public static int fromColorName(String name ) {
        return NAMES.indexOf(name);
    }

}
