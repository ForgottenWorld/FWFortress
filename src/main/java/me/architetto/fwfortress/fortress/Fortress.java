package me.architetto.fwfortress.fortress;


import me.architetto.fwfortress.config.SettingsHandler;
import org.bukkit.*;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Fortress {

    private String fortressName;

    private String firstOwner;
    private String currentOwner;

    private String worldName;
    private Vector fortressVector;

    private int fortressHP;

    private long lastBattle;
    private long lastRepair;

    public Fortress(String fortressName, String firstOwner, String currentOwner,
                    Location fortressLocation, int fortressHP, long lastBattle, long lastRepair) {

        this.fortressName = fortressName;
        this.firstOwner = firstOwner;
        this.currentOwner = currentOwner;
        this.fortressVector = fortressLocation.toVector().toBlockVector();
        this.worldName = fortressLocation.getWorld().getName();
        this.fortressHP = fortressHP;
        this.lastBattle = lastBattle;
        this.lastRepair = lastRepair;

    }


    public String getFortressName() { return this.fortressName; }

    public void setFortressName(String fortressName) { this.fortressName = fortressName; }

    public String getFirstOwner() { return this.firstOwner; }

    public String getCurrentOwner() { return this.currentOwner; }

    public void setCurrentOwner(String owner) { this.currentOwner = owner; }

    public int getFortressHP() { return this.fortressHP; }

    public void setFortressHP(int fortressHP) { this.fortressHP = fortressHP; }

    public long getLastBattle() { return this.lastBattle; }

    public void setLastBattle(long lastBattle) { this.lastBattle = lastBattle; }

    public long getLastRepair() { return this.lastRepair; }

    public void setLastRepair(long lastRepair) { this.lastRepair = lastRepair; }

    public Vector getFortressVector() { return this.fortressVector; }

    public String getWorldName() { return this.worldName; }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(this.worldName),this.fortressVector.getX(),this.fortressVector.getY(),this.fortressVector.getZ());
    }

    public String getFormattedLocation() {
        Location location = getLocation();
        return ChatColor.AQUA + "[X] " + ChatColor.YELLOW + location.getBlockX() +
                ChatColor.AQUA + " [Y] " + ChatColor.YELLOW + location.getBlockY() +
                ChatColor.AQUA + " [Z] " + ChatColor.YELLOW + location.getBlockZ() +
                ChatColor.AQUA + " [WORLD] " + ChatColor.YELLOW + location.getWorld().getName();
    }

    //Non utilizzare dirattemente nel listener
    public List<Long> getChunkKeys() {
        World world = Bukkit.getWorld(this.worldName);
        Chunk chunk = world.getChunkAt(getLocation());
        List<Long> chunkKeyList = new ArrayList<>();

        int cX = chunk.getX();
        int cZ = chunk.getZ();

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                chunkKeyList.add(world.getChunkAt(cX + x, cZ + z).getChunkKey());
            }
        }

        return chunkKeyList;

    }

    public BoundingBox getGreenBoundingBox() {

        Chunk c = getLocation().getChunk();

        int blockX = c.getX() << 4;
        int blockZ = c.getZ() << 4;

        int height = SettingsHandler.getInstance().getMaxGroundDistance();

        Location g1 = new Location(c.getWorld(),blockX,this.fortressVector.getBlockY() - 4,blockZ);
        Location g2 = new Location(c.getWorld(),blockX + 16,this.fortressVector.getBlockY() + height,blockZ +16);

        return new BoundingBox(g1.getX(), g1.getY(), g1.getZ(), g2.getX(), g2.getY(), g2.getZ());
    }

    public BoundingBox getBlueBoundingBox() {

        Chunk c = getLocation().getChunk();

        int blockX = c.getX() << 4;
        int blockZ = c.getZ() << 4;

        int height = SettingsHandler.getInstance().getMaxGroundDistance();

        Location b1 = new Location(c.getWorld(),blockX - 16,this.fortressVector.getBlockY() - 4,blockZ - 16);
        Location b2 = new Location(c.getWorld(),blockX + 32,this.fortressVector.getBlockY() + height,blockZ + 32);

        return new BoundingBox(b1.getX(), b1.getY(), b1.getZ(), b2.getX(), b2.getY(), b2.getZ());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fortress fort = (Fortress) o;
        return  Objects.equals(worldName, fort.worldName) &&
                Objects.equals(fortressVector, fort.fortressVector);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fortressName, firstOwner, currentOwner, worldName, fortressVector, fortressHP);
    }

}
