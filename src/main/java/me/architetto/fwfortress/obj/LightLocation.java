package me.architetto.fwfortress.obj;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.Objects;

public class LightLocation {

    private final Vector vector;
    private final String worldName;

    public LightLocation(Location location) {
        this.vector = location.toVector();
        this.worldName = location.getWorld().getName();
    }

    public Location loc() {
        return new Location(Bukkit.getWorld(worldName), vector.getX(), vector.getY(), vector.getZ());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LightLocation ll = (LightLocation) o;
        return Objects.equals(vector, ll.vector) &&
                Objects.equals(worldName, ll.worldName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vector, worldName);
    }

}
