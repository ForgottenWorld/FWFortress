package me.architetto.fwfortress.fortress;

import me.architetto.fwfortress.config.SettingsHandler;
import me.architetto.fwfortress.util.ChatFormatter;
import me.architetto.fwfortress.util.localization.Message;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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

        if(!playerCreationMode.contains(sender.getUniqueId())) {
            return;
        }

        if (FortressService.getInstance().getFortressContainer().values().stream()
                .filter(fortress -> fortress.getWorldName().equals(location.getWorld().getName()))
                .anyMatch(fortress -> fortress.getFortressVector()
                        .distance(location.toVector()) < SettingsHandler.getInstance().getDistanceBetweenFortresses())) {

            Message.ERR_FORTRESS_DISTANCE.send(sender, SettingsHandler.getInstance().getDistanceBetweenFortresses());

            removePlayerToFortressCreationMode(sender.getUniqueId());

            return;
        }

        addNewFortress(playerFortressNameCreation.get(sender.getUniqueId()),
                playerFortressOwnerCreation.get(sender.getUniqueId()),
                playerFortressOwnerCreation.get(sender.getUniqueId()),
                location,
                SettingsHandler.getInstance().getFortressHP(),
                0,0);

        spawnParticleEffectOnFortressCreation(location.clone());
        Message.SUCCESS_FORTRESS_CREATED.send(sender,this.playerFortressNameCreation.get(sender.getUniqueId()));
        removePlayerToFortressCreationMode(sender.getUniqueId());

    }

    public void addNewFortress(String fortressName, String firstOwner,
                               String currentOwner, Location fortressPosition,
                               int fortressHP, long lastBattle, long lastRepair) {

        Fortress fortress = new Fortress(fortressName, firstOwner, currentOwner,
                fortressPosition, fortressHP, lastBattle, lastRepair);

        FortressService.getInstance().getFortressContainer().put(fortressName, fortress);
        FortressService.getInstance().getProtectedChunkKeys().put(fortressName, fortress.getChunkKeys());

        FortressService.getInstance().saveFortress(fortress);

    }


    public void addNewFortress(String fortressName, String firstOwner,
                               String currentOwner, Location fortressPosition,
                               int fortressHP, long lastBattle, long lastRepair, List<Long> fortressChunkKeys) {

        Fortress fortress = new Fortress(fortressName, firstOwner, currentOwner,
                fortressPosition, fortressHP, lastBattle, lastRepair);

        FortressService.getInstance().getFortressContainer().put(fortressName, fortress);
        FortressService.getInstance().getProtectedChunkKeys().put(fortressName, fortressChunkKeys);

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

    public void spawnParticleEffectOnFortressCreation(Location loc) {

        loc.add(0.5,2,0.5);
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(255, 69, 0), 5);
        loc.getWorld().spawnParticle(Particle.REDSTONE,loc,10,dustOptions);

    }

}
