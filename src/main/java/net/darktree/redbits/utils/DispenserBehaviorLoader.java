package net.darktree.redbits.utils;

import net.minecraft.MinecraftVersion;

public class DispenserBehaviorLoader {

    public static boolean register() {

        String version = MinecraftVersion.field_25319.getName();
        String[] str = version.split("\\.");

        // Parse game version
        int b = str.length > 1 ? Integer.parseInt( str[ 1 ] ) : 0;
        int c = str.length > 2 ? Integer.parseInt( str[ 2 ] ) : 0;

        if( b != 16 ) {

            // Unsupported but... who cares! Lets try any way!
            if( b > 16 ) {
                return loadDefault();
            }else{
                return loadLegacy();
            }

        }else{

            if( c > 1 ) {
                return loadDefault();
            }else{
                return loadLegacy();
            }

        }

    }

    private static boolean loadDefault() {
        return (new MusicDispenserBehavior()).register();
    }

    private static boolean loadLegacy() {
        try {
            MusicDispenserBehavior dispenserBehavior = (MusicDispenserBehavior) Class.forName("net.darktree.redbits.MusicDispenserBehavior").newInstance();
            return dispenserBehavior.register();
        } catch (IllegalAccessException | ClassNotFoundException | InstantiationException e) {
            e.printStackTrace();
            return false;
        }
    }

}
