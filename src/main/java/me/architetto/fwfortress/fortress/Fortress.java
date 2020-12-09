package me.architetto.fwfortress.fortress;


import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
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

    public Fortress(String fortressName, String firstOwner, Location fortressLocation) {

        this.fortressName = fortressName;
        this.firstOwner = firstOwner;
        this.currentOwner = firstOwner;
        this.fortressVector = fortressLocation.toVector().toBlockVector();
        this.worldName = fortressLocation.getWorld().getName();
        this.fortressHP = 1000; //placeholder

    }

    public Fortress(String fortressName, String firstOwner, Location fortressLocation, int fortressHP) {

        this.fortressName = fortressName;
        this.firstOwner = firstOwner;
        this.currentOwner = firstOwner;
        this.fortressVector = fortressLocation.toVector().toBlockVector();
        this.worldName = fortressLocation.getWorld().getName();
        this.fortressHP = fortressHP; //placeholder

    }

    public String getFortressName() { return this.fortressName; }

    public void setFortressName(String fortressName) { this.fortressName = fortressName; }

    public String getFirstOwner() { return this.firstOwner; }

    public String getCurrentOwner() { return this.currentOwner; }

    public void setCurrentOwner(String owner) { this.currentOwner = owner; }

    public int getFortressHP() { return this.fortressHP; }

    public void setFortressHP(int fortressHP) { this.fortressHP = fortressHP; }

    public Vector getFortressVector() { return this.fortressVector; }

    public String getWorldName() { return this.worldName; }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(this.worldName),this.fortressVector.getX(),this.fortressVector.getY(),this.fortressVector.getZ());
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

        int blockX = c.getX() << 4; //c.getX() * 16
        int blockZ = c.getZ() << 4; //c.getZ() * 16

        Location g1 = c.getBlock(blockX, this.fortressVector.getBlockY(), blockZ).getLocation();
        Location g2 = c.getBlock(blockX + 16, this.fortressVector.getBlockY() + 25, blockZ + 16).getLocation();

        return new BoundingBox(g1.getX(), g1.getY(), g1.getZ(), g2.getX(), g2.getY(), g2.getZ());

    }

    public BoundingBox getBlueBoundingBox() {

        Chunk c = getLocation().getChunk();

        int blockX = c.getX() << 4; //c.getX() * 16
        int blockZ = c.getZ() << 4; //c.getZ() * 16

        Location b1 = c.getBlock(blockX - 16, this.fortressVector.getBlockY(), blockZ + 16).getLocation();
        Location b2 = c.getBlock(blockX + 32, this.fortressVector.getBlockY() + 25, blockZ + 32).getLocation();

        return new BoundingBox(b1.getX(), b1.getY(), b1.getZ(), b2.getX(), b2.getY(), b2.getZ());

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fortress fort = (Fortress) o;
        return Objects.equals(fortressName, fort.fortressName) &&
                Objects.equals(firstOwner, fort.firstOwner) &&
                Objects.equals(currentOwner, fort.currentOwner) &&
                Objects.equals(worldName, fort.worldName) &&
                Objects.equals(fortressVector, fort.fortressVector) &&
                Objects.equals(fortressHP, fort.fortressHP);
    }


    @Override
    public int hashCode() {
        return Objects.hash(fortressName, firstOwner, currentOwner, worldName, fortressVector, fortressHP);
    }

}
