package me.architetto.fwfortress.fortress;


import com.palmergames.bukkit.towny.object.Coord;
import com.palmergames.bukkit.towny.object.WorldCoord;
import me.architetto.fwfortress.config.SettingsHandler;
import me.architetto.fwfortress.obj.LightLocation;
import org.bukkit.*;
import org.bukkit.util.BoundingBox;

import java.util.*;


public class Fortress {

    private final String name;
    private final LightLocation lightLocation;
    private final Set<Long> chunkKeys;

    private String owner;

    private long lastBattle;
    private long experience;

    private boolean enabled;

    public Fortress(String name, String owner, Location location, long lastBattle,
                    long experience, List<Long> chunkKeys, boolean enabled) {

        this.name = name;
        this.owner = owner;
        this.lightLocation = new LightLocation(location);
        this.lastBattle = lastBattle;
        this.chunkKeys = new HashSet<>(chunkKeys);
        this.experience = experience;
        this.enabled = enabled;

    }

    public String getName() { return this.name; }

    public String getOwner() { return this.owner; }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public long getLastBattle() { return this.lastBattle; }

    public void setLastBattle(long lastBattle) { this.lastBattle = lastBattle; }

    public long getExperience() {
        return this.experience;
    }

    public void setExperience(long experience) {
        this.experience = experience;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Location getLocation() {
        return lightLocation.loc().toCenterLocation();
    }

    public LightLocation getLightLocation() {
        return lightLocation;
    }

    public String getFormattedLocation() {
        Location location = lightLocation.loc();
        return ChatColor.AQUA + "[X] " + ChatColor.YELLOW + location.getBlockX() +
                ChatColor.AQUA + " [Y] " + ChatColor.YELLOW + location.getBlockY() +
                ChatColor.AQUA + " [Z] " + ChatColor.YELLOW + location.getBlockZ() +
                ChatColor.AQUA + " [WORLD] " + ChatColor.YELLOW + location.getWorld().getName();
    }

    public Coord getCoord() {
        return WorldCoord.parseCoord(lightLocation.loc());
    }

    public BoundingBox getGreenBoundingBox() {

        Location location = lightLocation.loc();
        Chunk c = location.getChunk();

        int blockX = c.getX() << 4;
        int blockZ = c.getZ() << 4;

        int maxHeight = SettingsHandler.getInstance().getMaxGroundDistance();

        Location g1 = new Location(c.getWorld(),blockX - 16,location.getBlockY() - 4,blockZ - 16);
        Location g2 = new Location(c.getWorld(),blockX + 32,location.getBlockY() + maxHeight,blockZ + 32);

        return new BoundingBox(g1.getX(), g1.getY(), g1.getZ(), g2.getX(), g2.getY(), g2.getZ());
    }

    public BoundingBox getBlueBoundingBox() {

        Location location = lightLocation.loc();
        Chunk c = location.getChunk();

        int blockX = c.getX() << 4;
        int blockZ = c.getZ() << 4;

        int height = SettingsHandler.getInstance().getMaxGroundDistance();

        Location b1 = new Location(c.getWorld(),blockX - 32,location.getBlockY() - 4,blockZ - 32);
        Location b2 = new Location(c.getWorld(),blockX + 48,location.getBlockY() + height,blockZ + 48);

        return new BoundingBox(b1.getX(), b1.getY(), b1.getZ(), b2.getX(), b2.getY(), b2.getZ());
    }

    public Set<Long> getCunkKeys() {
        return this.chunkKeys;
    }

    public String getFormattedName() {
        return name.replace("_"," ");
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fortress fort = (Fortress) o;
        return  Objects.equals(name, fort.name) &&
                Objects.equals(lightLocation, fort.lightLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, lightLocation);
    }
}
