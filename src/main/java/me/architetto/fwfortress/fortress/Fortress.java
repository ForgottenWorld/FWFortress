package me.architetto.fwfortress.fortress;


import com.palmergames.bukkit.towny.object.Coord;
import com.palmergames.bukkit.towny.object.WorldCoord;
import me.architetto.fwfortress.config.SettingsHandler;
import org.bukkit.*;
import org.bukkit.util.BoundingBox;

import java.util.*;


public class Fortress {

    private final String name;
    private final Location position;
    private final Set<Long> chunkKeys;

    private String owner;

    private long lastBattle;
    private long experience;

    private boolean enabled;

    public Fortress(String name, String owner, Location position, long lastBattle,
                    long experience, List<Long> chunkKeys, boolean enabled) {

        this.name = name;
        this.owner = owner;
        this.position = position;
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
        return position.toCenterLocation();
    }

    public String getFormattedLocation() {
        return ChatColor.AQUA + "[X] " + ChatColor.YELLOW + position.getBlockX() +
                ChatColor.AQUA + " [Y] " + ChatColor.YELLOW + position.getBlockY() +
                ChatColor.AQUA + " [Z] " + ChatColor.YELLOW + position.getBlockZ() +
                ChatColor.AQUA + " [WORLD] " + ChatColor.YELLOW + position.getWorld().getName();
    }

    public Coord getCoord() {
        return WorldCoord.parseCoord(position);
    }

    public BoundingBox getGreenBoundingBox() {

        Chunk c = this.position.getChunk();

        int blockX = c.getX() << 4;
        int blockZ = c.getZ() << 4;

        int maxHeight = SettingsHandler.getInstance().getMaxGroundDistance();

        Location g1 = new Location(c.getWorld(),blockX - 16,this.position.getBlockY() - 4,blockZ - 16);
        Location g2 = new Location(c.getWorld(),blockX + 32,this.position.getBlockY() + maxHeight,blockZ + 32);

        return new BoundingBox(g1.getX(), g1.getY(), g1.getZ(), g2.getX(), g2.getY(), g2.getZ());
    }

    public BoundingBox getBlueBoundingBox() {

        Chunk c = this.position.getChunk();

        int blockX = c.getX() << 4;
        int blockZ = c.getZ() << 4;

        int height = SettingsHandler.getInstance().getMaxGroundDistance();

        Location b1 = new Location(c.getWorld(),blockX - 32,this.position.getBlockY() - 4,blockZ - 32);
        Location b2 = new Location(c.getWorld(),blockX + 48,this.position.getBlockY() + height,blockZ + 48);

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
                Objects.equals(position, fort.position);
    }
}
