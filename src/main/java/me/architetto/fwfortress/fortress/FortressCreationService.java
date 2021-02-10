package me.architetto.fwfortress.fortress;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import me.architetto.fwfortress.config.SettingsHandler;
import me.architetto.fwfortress.localization.Message;
import me.architetto.fwfortress.util.LocationUtil;
import me.architetto.fwfortress.util.TownyUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.*;

public class FortressCreationService {

    private static FortressCreationService fortressCreationService;

    private HashMap<UUID, String> playerFortressNameCreation;
    private HashMap<UUID, String> playerFortressOwnerCreation;
    private List<UUID> playerCreationMode;

    private FortressCreationService() {
        if(fortressCreationService != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }

        this.playerFortressNameCreation = new HashMap<>();
        this.playerFortressOwnerCreation = new HashMap<>();
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

        if (!checkFortressesDistance(sender,location)) {
            removePlayerToFortressCreationMode(sender.getUniqueId());
            return;
        }

        if (!checkTownDistance(sender, location)) {
            removePlayerToFortressCreationMode(sender.getUniqueId());
            return;
        }

        addNewFortress(playerFortressNameCreation.get(sender.getUniqueId()),
                System.currentTimeMillis(),
                playerFortressOwnerCreation.get(sender.getUniqueId()),
                playerFortressOwnerCreation.get(sender.getUniqueId()),
                location,
                SettingsHandler.getInstance().getFortressHP(),
                0,0, LocationUtil.area3x3ChunkKeys(location));


        FortressParticleEffects.getInstance().fortressGreenAreaEffect(location);

        FortressParticleEffects.getInstance().fortressBlueAreaEffect(location);

        Message.SUCCESS_FORTRESS_CREATED.send(sender,this.playerFortressNameCreation.get(sender.getUniqueId()));

        removePlayerToFortressCreationMode(sender.getUniqueId());

    }

    public void fortressClaimMethod(Player player, String fortressName) {

        Resident resident = TownyUtil.getResidentFromPlayerName(player.getName());

        if (Objects.isNull(resident))
            return;

        Town senderTown = TownyUtil.getTownFromPlayerName(player.getName());

        if (Objects.isNull(senderTown) || !resident.isMayor()) {
            Message.ERR_CLAIM_MAYOR_ONLY.send(player);
            return;
        }

        if (FortressService.getInstance().getFortressContainer().stream().anyMatch(f -> f.getFirstOwner().equals(senderTown.getName()))) {
            Message.ERR_TOWN_ALREADY_BUILD_FORTRESS.send(player,senderTown.getFormattedName());
            return;
        }

        Location location = player.getLocation();

        if (!checkFortressesDistance(player, location))
            return;

        if  (!checkTownDistance(player, location))
            return;

        Optional<Fortress> optionalFortress = FortressService.getInstance().getFortress(fortressName);

        if (optionalFortress.isPresent()) {
            Message.ERR_FORTRESS_NAME_ALREADY_EXIST.send(player);
            return;
        }

        addNewFortress(fortressName,
                System.currentTimeMillis(),
                senderTown.getName(),
                senderTown.getName(),
                location,
                SettingsHandler.getInstance().getFortressHP(),
                0,
                0,
                LocationUtil.area3x3ChunkKeys(location));

        FortressParticleEffects.getInstance().fortressGreenAreaEffect(location);

        FortressParticleEffects.getInstance().fortressBlueAreaEffect(location);

        Message.SUCCESS_FORTRESS_CLAIM_BRADCAST.broadcast(senderTown.getFormattedName(),fortressName.replace("_"," "));
    }

    public void loadFortress(String fortressName, long creationDate ,String firstOwner,
                             String currentOwner, Location fortressPosition,
                             int fortressHP, long lastBattle, long lastRepair, List<Long> chunkKeys) {

        Fortress fortress = new Fortress(fortressName, creationDate ,firstOwner, currentOwner,
                fortressPosition, fortressHP, lastBattle, lastRepair, chunkKeys);

        FortressService.getInstance().getFortressContainer().add(fortress);

    }

    public void addNewFortress(String fortressName, long creationDate ,String firstOwner,
                               String currentOwner, Location fortressPosition,
                               int fortressHP, long lastBattle, long lastRepair, List<Long> chunkKeys) {

        Fortress fortress = new Fortress(fortressName, creationDate ,firstOwner, currentOwner,
                fortressPosition, fortressHP, lastBattle, lastRepair, chunkKeys);

        FortressService.getInstance().getFortressContainer().add(fortress);

        FortressService.getInstance().saveFortress(fortress);

    }


    public void addPlayerToFortressCreationMode(Player player, String fortressName, String fortressOwner) {

        this.playerCreationMode.add(player.getUniqueId());
        this.playerFortressNameCreation.put(player.getUniqueId(), fortressName);
        this.playerFortressOwnerCreation.put(player.getUniqueId(), fortressOwner);
    }

    public void removePlayerToFortressCreationMode(UUID playerUUID) {

        this.playerCreationMode.remove(playerUUID);
        this.playerFortressNameCreation.remove(playerUUID);
        this.playerFortressOwnerCreation.remove(playerUUID);
    }

    public boolean isPlayerInFortressCreationMode(Player player) {
        return this.playerCreationMode.contains(player.getUniqueId());
    }

    public boolean checkDistance(Location locA, Location locB) {

        if (!locA.getWorld().getName().equals(locB.getWorld().getName()))
            return true;

        return locA.toVector()
                .distance(locB.toVector()) > SettingsHandler.getInstance().getDistanceBetweenFortresses();

    }

    public boolean checkFortressesDistance(Player sender, Location location) {
        Optional<Fortress> optFortress = FortressService.getInstance().getFortressContainer()
                .stream().filter(f -> !checkDistance(f.getLocation(),location)).findFirst();

        if (optFortress.isPresent()) {
            Message.ERR_FORTRESS_DISTANCE.send(sender, optFortress.get().getFormattedName(), SettingsHandler.getInstance().getDistanceBetweenFortresses());
            return false;
        }

        return true;
    }

    public boolean checkTownDistance(Player sender, Location location) {


        for (Town town : TownyAPI.getInstance().getDataSource().getTowns()) {

            Location spawn = null;

            try {
                spawn = town.getSpawn();
            } catch (TownyException e) {
                e.printStackTrace();
            }

            if (spawn != null && !checkDistance(spawn,location)) {
                Message.ERR_FORTRESS_DISTANCE.send(sender,
                        town.getFormattedName() ,
                        SettingsHandler.getInstance().getDistanceBetweenFortresses());
                return false;
            }

        }
        return true;
    }


}
