package me.architetto.fwfortress.util;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class LocationUtil {

    public static List<Long> area3x3ChunkKeys(Location location) {
        World world = location.getWorld();
        Chunk chunk = world.getChunkAt(location);
        List<Long> chunkKeyList = new ArrayList<>();

        int cX = chunk.getX();
        int cZ = chunk.getZ();

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                long key = world.getChunkAt(cX + x, cZ + z).getChunkKey();
                chunkKeyList.add(key);
            }
        }
        return chunkKeyList;
    }
}
