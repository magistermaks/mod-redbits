package net.darktree.redbits.utils;

import java.util.ArrayList;

public class ColorProvider {

	private static final int BASE = 0x33;
	public static final int COUNT = 15;

	private static final ArrayList<String> NAMES = new ArrayList<>();

	private static final int[] COLORS = {
			make( 0x1D1D21, "black" ),
			make( 0xB02E26, "red" ),
			make( 0x5E7C16, "green" ),
			make( 0x835432, "brown" ),
			make( 0x3C44AA, "blue" ),
			make( 0x8932B8, "purple" ),
			make( 0x169C9C, "cyan" ),
			make( 0x9D9D97, "light_gray" ),
			make( 0x474F52, "gray" ),
			make( 0xF38BAA, "pink" ),
			make( 0x80C71F, "lime" ),
			make( 0xFED83D, "yellow" ),
			make( 0x3AB3DA, "light_blue" ),
			make( 0xC74EBD, "magenta" ),
			make( 0xF9801D, "orange" ),
			make( 0xF9FFFE, "white" )
	};

	private static int make( int baseColor, String name ) {
		int r = Math.min( ((baseColor >> 16) & 0xFF) + BASE, 0xFF );
		int g = Math.min( ((baseColor >> 8) & 0xFF) + BASE, 0xFF );
		int b = Math.min( (baseColor & 0xFF) + BASE, 0xFF );

		NAMES.add(name);
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
