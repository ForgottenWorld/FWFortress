package me.architetto.fwfortress.fortress;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.WorldCoord;
import me.architetto.fwfortress.config.SettingsHandler;
import me.architetto.fwfortress.localization.Message;
import me.architetto.fwfortress.util.CoordUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class FortressCreationService {

    private static FortressCreationService fortressCreationService;

    private HashMap<UUID, String> playerFortressNameCreation;
    private List<UUID> playerCreationMode;

    private FortressCreationService() {
        if(fortressCreationService != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }

        this.playerFortressNameCreation = new HashMap<>();
        this.playerCreationMode = new ArrayList<>();

    }

    public static FortressCreationService getInstance() {
        if(fortressCreationService == null) {
            fortressCreationService = new FortressCreationService();
        }
        return fortressCreationService;
    }

    public void fortressCreationMethod(Player sender, Location location) {

        if(!playerCreationMode.contains(sender.getUniqueId()))
            return;

        SettingsHandler settingsHandler = SettingsHandler.getInstance();

        WorldCoord fortCoord = WorldCoord.parseWorldCoord(location);

        Set<String> townName = checkTownDistance(fortCoord);

        if (townName.size() != 0) {
            Message.ERR_TOWN_DISTANCE.send(sender,settingsHandler.getMinFortressDistance(),townName);
            removePlayerToFortressCreationMode(sender.getUniqueId());
            return;
        }

        Set<String> fortressName = checkFortressDistance(fortCoord);

        if (fortressName.size() != 0) {
            Message.ERR_FORTRESS_DISTANCE.send(sender,settingsHandler.getMinFortressDistance(),fortressName);
            removePlayerToFortressCreationMode(sender.getUniqueId());
            return;
        }

        newFortress(playerFortressNameCreation.get(sender.getUniqueId()),
                location,
                area5x5ChunkKeys(location));

        FortressParticleEffects.getInstance().fortressGreenAreaEffect(location);

        FortressParticleEffects.getInstance().fortressBlueAreaEffect(location);

        Message.SUCCESS_FORTRESS_CREATED.send(sender,this.playerFortressNameCreation.get(sender.getUniqueId()));

        removePlayerToFortressCreationMode(sender.getUniqueId());

    }

    public void loadFortress(String fortressName, String owner, Location fortressPosition,
                             long lastBattle, long experience, List<Long> chunkKeys, boolean enabled) {

        Fortress fortress = new Fortress(fortressName, owner, fortressPosition, lastBattle, experience, chunkKeys, enabled);

        FortressService.getInstance().getFortressContainer().add(fortress);

    }

    public void newFortress(String fortressName, Location fortressPosition, List<Long> chunkKeys) {

        Fortress fortress = new Fortress(fortressName, null, fortressPosition,0, 0 , chunkKeys, true);

        FortressService.getInstance().getFortressContainer().add(fortress);

        FortressService.getInstance().saveFortress(fortress);

    }


    public void addPlayerToFortressCreationMode(Player player, String fortressName) {

        this.playerCreationMode.add(player.getUniqueId());
        this.playerFortressNameCreation.put(player.getUniqueId(), fortressName);
    }

    public void removePlayerToFortressCreationMode(UUID playerUUID) {

        this.playerCreationMode.remove(playerUUID);
        this.playerFortressNameCreation.remove(playerUUID);
    }

    public boolean isPlayerInFortressCreationMode(Player player) {
        return this.playerCreationMode.contains(player.getUniqueId());
    }

    public Set<String> checkTownDistance(WorldCoord worldCoord) {
        SettingsHandler settingsHandler = SettingsHandler.getInstance();

        return TownyAPI.getInstance().getDataSource().getTowns()
                .stream()
                .filter(t-> {
                    TownBlock townBlock = null;
                    try {
                        townBlock = t.getHomeBlock();
                    } catch (TownyException ignored) {}

                    return townBlock != null && worldCoord.getBukkitWorld().getName().equals(townBlock.getWorldCoord().getWorldName())
                            && CoordUtil.distance(townBlock.getCoord(), worldCoord.getCoord()) < settingsHandler.getMinFortressDistance();
                })
                .map(Town::getFormattedName)
                .collect(Collectors.toSet());

    }

    public Set<String> checkFortressDistance(WorldCoord worldCoord) {
        SettingsHandler settingsHandler = SettingsHandler.getInstance();

        return FortressService.getInstance().getFortressContainer()
                .stream()
                .filter(f -> f.getLocation().getWorld().getName().equals(worldCoord.getWorldName()))
                .filter(f -> CoordUtil.distance(worldCoord.getCoord(),f.getCoord()) < settingsHandler.getMinFortressDistance())
                .map(Fortress::getFormattedName)
                .collect(Collectors.toSet());

    }

    private List<Long> area5x5ChunkKeys(Location location) {
        World world = location.getWorld();
        Chunk chunk = world.getChunkAt(location);
        List<Long> chunkKeyList = new ArrayList<>();

        int cX = chunk.getX();
        int cZ = chunk.getZ();

        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                long key = world.getChunkAt(cX + x, cZ + z).getChunkKey();
                chunkKeyList.add(key);
            }
        }
        return chunkKeyList;
    }
}
