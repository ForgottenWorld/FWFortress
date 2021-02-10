package me.architetto.fwfortress.fortress;


import me.architetto.fwfortress.config.SettingsHandler;
import org.bukkit.*;
import org.bukkit.util.BoundingBox;

import java.util.*;


public class Fortress {

    private final Location fortressLocation;
    private final List<Long> chunkKeys;

    private String fortressName;

    private String firstOwner;
    private String currentOwner;

    private int currentHP;

    private long lastBattle;
    private long lastRepair;
    private final long creationDate;

    public Fortress(String fortressName, long creationDate ,String firstOwner, String currentOwner,
                    Location fortressLocation, int currentHP, long lastBattle, long lastRepair, List<Long> chunkKeys) {

        this.creationDate = creationDate;
        this.fortressName = fortressName;
        this.firstOwner = firstOwner;
        this.currentOwner = currentOwner;
        this.fortressLocation = fortressLocation;
        this.currentHP = currentHP;
        this.lastBattle = lastBattle;
        this.lastRepair = lastRepair;
        this.chunkKeys = chunkKeys;

    }

    public String getFortressName() { return this.fortressName; }

    public void setFortressName(String fortressName) { this.fortressName = fortressName; }

    public String getFirstOwner() { return this.firstOwner; }

    public String getCurrentOwner() { return this.currentOwner; }

    public void setCurrentOwner(String owner) { this.currentOwner = owner; }

    public int getCurrentHP() { return this.currentHP; }

    public void setCurrentHP(int currentHP) { this.currentHP = currentHP; }

    public long getLastBattle() { return this.lastBattle; }

    public void setLastBattle(long lastBattle) { this.lastBattle = lastBattle; }

    public long getLastRepair() { return this.lastRepair; }

    public void setLastRepair(long lastRepair) { this.lastRepair = lastRepair; }

    public Location getLocation() {
        return this.fortressLocation;
    }

    public long getCreationDate() {
        return this.creationDate;
    }

    public String getFormattedLocation() {
        return ChatColor.AQUA + "[X] " + ChatColor.YELLOW + fortressLocation.getBlockX() +
                ChatColor.AQUA + " [Y] " + ChatColor.YELLOW + fortressLocation.getBlockY() +
                ChatColor.AQUA + " [Z] " + ChatColor.YELLOW + fortressLocation.getBlockZ() +
                ChatColor.AQUA + " [WORLD] " + ChatColor.YELLOW + fortressLocation.getWorld().getName();
    }

    public BoundingBox getGreenBoundingBox() {

        Chunk c = this.fortressLocation.getChunk();

        int blockX = c.getX() << 4;
        int blockZ = c.getZ() << 4;

        int maxHeight = SettingsHandler.getInstance().getMaxGroundDistance();

        Location g1 = new Location(c.getWorld(),blockX,this.fortressLocation.getBlockY() - 4,blockZ);
        Location g2 = new Location(c.getWorld(),blockX + 16,this.fortressLocation.getBlockY() + maxHeight,blockZ +16);

        return new BoundingBox(g1.getX(), g1.getY(), g1.getZ(), g2.getX(), g2.getY(), g2.getZ());
    }

    public BoundingBox getBlueBoundingBox() {

        Chunk c = this.fortressLocation.getChunk();

        int blockX = c.getX() << 4;
        int blockZ = c.getZ() << 4;

        int height = SettingsHandler.getInstance().getMaxGroundDistance();

        Location b1 = new Location(c.getWorld(),blockX - 16,this.fortressLocation.getBlockY() - 4,blockZ - 16);
        Location b2 = new Location(c.getWorld(),blockX + 32,this.fortressLocation.getBlockY() + height,blockZ + 32);

        return new BoundingBox(b1.getX(), b1.getY(), b1.getZ(), b2.getX(), b2.getY(), b2.getZ());
    }

    public List<Long> getCunkKeys() {
        return this.chunkKeys;
    }

    public String getFormattedName() {
        return fortressName.replace("_"," ");
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fortress fort = (Fortress) o;
        return  Objects.equals(fortressName, fort.fortressName) &&
                Objects.equals(fortressLocation, fort.fortressLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fortressName, firstOwner, currentOwner, fortressLocation, currentHP);
    }

}
