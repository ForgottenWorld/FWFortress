package me.architetto.fwfortress.util;

import com.palmergames.bukkit.towny.object.Coord;

public class CoordUtil {

    public static double distance(Coord a, Coord b) {
        return Math.sqrt(Math.pow(b.getX() - a.getX(),2) + Math.pow(b.getZ() - a.getZ(),2));
    }

}
