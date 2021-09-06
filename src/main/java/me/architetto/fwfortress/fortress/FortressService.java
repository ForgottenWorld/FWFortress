package me.architetto.fwfortress.fortress;

import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.WorldCoord;
import me.architetto.fwfortress.FWFortress;
import me.architetto.fwfortress.config.ConfigManager;
import me.architetto.fwfortress.localization.Message;
import me.architetto.fwfortress.util.CoordUtil;
import org.bukkit.*;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class FortressService {

    private static FortressService fortressService;

    private final HashMap<UUID, HashMap<Long, Fortress>> fortressByChunkCoordsContainer;

    private final HashMap<String, Fortress> fortressByNameContainer;

    private final HashMap<String, List<Fortress>> fortressesByTownName;

    private FortressService() {
        if(fortressService != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }

        this.fortressByChunkCoordsContainer = new HashMap<>();
        this.fortressByNameContainer = new HashMap<>();
        this.fortressesByTownName = new HashMap<>();
    }

    public static FortressService getInstance() {
        if(fortressService == null) {
            fortressService = new FortressService();
        }
        return fortressService;
    }

    public void saveFortress(Fortress fortress) {

        Bukkit.getScheduler().scheduleSyncDelayedTask(FWFortress.plugin, () -> {

            ConfigManager configManager = ConfigManager.getInstance();

            configManager.setData(configManager.getConfig("Fortress.yml"),
                    fortress.getName() + ".OWNER", fortress.getOwner());
            configManager.addLocation(configManager.getConfig("Fortress.yml"),
                    fortress.getLocation(),fortress.getName() + ".FORTRESS_POSITION");
            configManager.setData(configManager.getConfig("Fortress.yml"),
                    fortress.getName() + ".EXPERIENCE", fortress.getExperience());
            configManager.setData(configManager.getConfig("Fortress.yml"),
                    fortress.getName() + ".LAST_BATTLE", fortress.getLastBattle());
            configManager.setData(configManager.getConfig("Fortress.yml"),
                    fortress.getName() + ".ENABLED", fortress.isEnabled());
            configManager.setData(configManager.getConfig("Fortress.yml"),
                    fortress.getName() + ".CHUNKKEYS",
                     new ArrayList<>(fortress.getChunkKeys()));

        });
    }

    public void updateFortress(Fortress fortress) {

        Bukkit.getScheduler().scheduleSyncDelayedTask(FWFortress.plugin, () -> {

            ConfigManager configManager = ConfigManager.getInstance();

            configManager.setData(configManager.getConfig("Fortress.yml"),
                    fortress.getName() + ".OWNER", fortress.getOwner());
            configManager.setData(configManager.getConfig("Fortress.yml"),
                    fortress.getName() + ".EXPERIENCE", fortress.getExperience());
            configManager.setData(configManager.getConfig("Fortress.yml"),
                    fortress.getName() + ".LAST_BATTLE", fortress.getLastBattle());
            configManager.setData(configManager.getConfig("Fortress.yml"),
                    fortress.getName() + ".ENABLED", fortress.isEnabled());

        });

    }

    public Optional<Fortress> getFortress(String name) {
        return Optional.ofNullable(fortressByNameContainer.get(name));
    }

    public Optional<Fortress> getFortress(long chunkKey, UUID worldUUID) {
        HashMap<Long, Fortress> fortresses = fortressByChunkCoordsContainer.get(worldUUID);
        return fortresses != null ? Optional.ofNullable(fortresses.get(chunkKey)) : Optional.empty();
    }

    public List<String> getFortressNames() {
        return new ArrayList<>(fortressByNameContainer.keySet());
    }

    public void addFortress(Fortress fortress) {
        fortressByNameContainer.put(fortress.getName(), fortress);
        String townName = fortress.getOwner();
        if (townName != null) {
            List<Fortress> townFortresses = fortressesByTownName.computeIfAbsent(
                    townName,
                    k -> new ArrayList<>()
            );
            townFortresses.add(fortress);
        }
        UUID worldUUID = fortress.getLightLocation().getWorldUUID();
        Map<Long, Fortress> worldFortresses = fortressByChunkCoordsContainer.computeIfAbsent(
                worldUUID,
                k -> new HashMap<>()
        );
        for (Long key : fortress.getChunkKeys()) {
            worldFortresses.put(key, fortress);
        }
    }

    public void dispossessFortressesFromTown(Town town) {
        List<Fortress> fortresses = fortressesByTownName.remove(town.getName());
        if (fortresses == null) return;
        for (Fortress fortress : fortresses) {
            fortress.setOwner(null);
            updateFortress(fortress);
            Message.FORTRESS_RETURN_FREE.broadcast(fortress.getFormattedName(), town.getFormattedName());
        }
    }

    public void handleTownRename(String oldName, String newName) {
        if (oldName.equals(newName)) return;
        List<Fortress> fortresses = fortressesByTownName.remove(oldName);
        if (fortresses == null) return;
        for (Fortress fortress : fortresses) {
            fortress.setOwner(newName);
            updateFortress(fortress);

            // LOG
            Bukkit.getLogger().log(
                    Level.INFO,
                    ChatColor.YELLOW + "[RenameTownEvent]" +
                            ChatColor.RESET + " Changed fortress owner name from " +
                            ChatColor.YELLOW + oldName +
                            ChatColor.RESET + " to " +
                            ChatColor.YELLOW + newName
            );
        }
        fortressesByTownName.put(newName, fortresses);
    }

    public Set<String> getFortressesWithinDistanceFromCoord(WorldCoord coord, int distance) {
        World world = coord.getBukkitWorld();
        if (world == null) return new HashSet<>();
        HashMap<Long, Fortress> worldFortresses = fortressByChunkCoordsContainer.get(world.getUID());
        if (worldFortresses == null) return new HashSet<>();
        return worldFortresses.values()
                .stream()
                .distinct()
                .filter(f -> CoordUtil.distance(coord.getCoord(),f.getCoord()) < distance)
                .map(Fortress::getFormattedName)
                .collect(Collectors.toSet());
    }

    public void clearFortressContainer() {
        this.fortressByChunkCoordsContainer.clear();
    }

    public void removeFortress(Fortress fortress) {

        ConfigManager configManager = ConfigManager.getInstance();

        ConfigManager.getInstance().setData(configManager.getConfig("Fortress.yml"),fortress.getName(),null);

        String townName = fortress.getOwner();
        if (townName != null) {
            List<Fortress> townFortresses = fortressesByTownName.get(townName);
            if (townFortresses != null) {
                townFortresses.remove(fortress);
                if (townFortresses.isEmpty()) {
                    fortressesByTownName.remove(townName);
                }
            }
        }
        UUID worldUUID = fortress.getLightLocation().getWorldUUID();
        HashMap<Long, Fortress> worldFortresses = fortressByChunkCoordsContainer.get(worldUUID);
        if (worldFortresses != null) {
            for (Long key : fortress.getChunkKeys()) {
                worldFortresses.remove(key);
            }
            if (worldFortresses.isEmpty()) {
                fortressByChunkCoordsContainer.remove(worldUUID);
            }
        }
        fortressByNameContainer.remove(fortress.getName());
    }

    public int countFortressesOwnedByTown(String townName) {
        return this.fortressesByTownName.getOrDefault(townName, new ArrayList<>()).size();
    }

    public List<Fortress> getFortressesOwnedByTown(String townName) {
        return this.fortressesByTownName.getOrDefault(townName, new ArrayList<>());
    }

    public void updateFortressByTownContainer(String oldOwner, String newOwner, Fortress fortress) {
        List<Fortress> townFortresses = fortressesByTownName.computeIfAbsent(
                newOwner,
                k -> new ArrayList<>()
        );
        if (oldOwner != null)
            this.fortressesByTownName.get(oldOwner).remove(fortress);
        townFortresses.add(fortress);

    }

}
