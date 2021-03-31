package me.architetto.fwfortress.obj;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.UUID;

public class LightLocation {

    private final Vector vector;
    private final UUID worldUUID;

    public LightLocation(Location location) {
        this.vector = location.toVector();
        this.worldUUID = location.getWorld().getUID();
    }

    public UUID getWorldUUID() {
        return worldUUID;
    }

    public Vector getVector() {
        return vector;
    }

    public Location loc() {
        return new Location(Bukkit.getWorld(worldUUID), vector.getX(), vector.getY(), vector.getZ());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LightLocation ll = (LightLocation) o;
        return Objects.equals(vector, ll.vector) &&
                Objects.equals(worldUUID, ll.worldUUID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vector, worldUUID);
    }

}
