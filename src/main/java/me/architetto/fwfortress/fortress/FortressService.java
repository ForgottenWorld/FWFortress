package me.architetto.fwfortress.fortress;

import me.architetto.fwfortress.config.ConfigManager;
import me.architetto.fwfortress.util.ChatFormatter;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;

public class FortressService {

    private static FortressService fortressService;

    private HashMap<String,Fortress> fortressContainer;

    private HashMap<String,List<Long>> fortressChunkKey; //todo: vanno inseriti tutti i chunkkey qui

    private HashMap<UUID, String> playerFortressNameCreation;
    private HashMap<UUID, String> playerFortressOwnerCreation;

    private List<UUID> playerCreationMode;

    private FortressService() {
        if(fortressService != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }

        this.fortressContainer = new HashMap<>();

        this.fortressChunkKey = new HashMap<>();

        this.playerFortressNameCreation = new HashMap<>();
        this.playerFortressOwnerCreation = new HashMap<>();

        this.playerCreationMode = new ArrayList<>();

    }

    public static FortressService getInstance() {
        if(fortressService == null) {
            fortressService = new FortressService();
        }
        return fortressService;
    }

    public boolean newFortress(String fortressName, String firstOwner, Location fortressPosition) {

        if (fortressContainer.containsKey(fortressName)) {
            return false;
        }

        Fortress fortress = new Fortress(fortressName, firstOwner, fortressPosition);

        fortressContainer.put(fortressName, fortress);

        this.fortressChunkKey.put(fortressName,fortress.getChunkKeys());

        ConfigManager configManager = ConfigManager.getInstance();

        configManager.setData(configManager.getConfig("Fortress.yml"),fortressName + ".FIRTS_OWNER",firstOwner);
        configManager.setData(configManager.getConfig("Fortress.yml"),fortressName + ".OWNER",firstOwner);
        configManager.setData(configManager.getConfig("Fortress.yml"),fortressName + ".FORTRESS_WORLD",fortressPosition.getWorld().getName());
        configManager.setData(configManager.getConfig("Fortress.yml"),fortressName + ".FORTRESS_POSITION",fortressPosition);
        configManager.setData(configManager.getConfig("Fortress.yml"),fortressName + ".FORTRESS_HP",1000); //placeholder

        return true;
    }

    public void fortressCrationHandler(Player sender, Location location) {
        if(!playerCreationMode.contains(sender.getUniqueId())) {
            return;
        }



        if (newFortress(playerFortressNameCreation.get(sender.getUniqueId()),
                playerFortressOwnerCreation.get(sender.getUniqueId()),
                location)) {

            spawnEffectAtBlock(location);
            sender.sendMessage(ChatFormatter.formatSuccessMessage("Fortezza inserita con successo"));
            sender.sendMessage(ChatFormatter.formatSuccessMessage("Nome fortezza : ") + playerFortressNameCreation.get(sender.getUniqueId()));
            sender.sendMessage(ChatFormatter.formatSuccessMessage("Proprietario fortezza : ") + playerFortressOwnerCreation.get(sender.getUniqueId()));

        } else
            sender.sendMessage(ChatFormatter.formatErrorMessage("Errore nell'inserimento dell'arena. Controlla i parametri inseriti"));

        this.playerFortressNameCreation.remove(sender.getUniqueId());
        this.playerFortressOwnerCreation.remove(sender.getUniqueId());
        this.playerCreationMode.remove(sender.getUniqueId());

    }

    public void addPlayerToFortressCreation(Player player, String fortressName, String fortressOwner) {

        this.playerCreationMode.add(player.getUniqueId());
        this.playerFortressNameCreation.put(player.getUniqueId(), fortressName);
        this.playerFortressOwnerCreation.put(player.getUniqueId(), fortressOwner);
    }

    public boolean isPlayerInCreationMode(Player player) {
        return this.playerCreationMode.contains(player.getUniqueId());
    }

    public Optional<Fortress> getFortress(String name) {
        return fortressContainer.containsKey(name) ? Optional.of(fortressContainer.get(name)) : Optional.empty();
    }

    public HashMap<String, Fortress> getFortressContainer() {
        return this.fortressContainer;
    }

    public void addProtectedChunkKeys(Fortress fortress) {
        this.fortressChunkKey.put(fortress.getFortressName(), fortress.getChunkKeys());
    }

    public HashMap<String,List<Long>> getProtectedChunkKeys() {
        return this.fortressChunkKey;
    }

    public void spawnEffectAtBlock(Location loc) {

        loc.add(0.5,1,0.5);
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(255, 69, 0), 5);
        loc.getWorld().spawnParticle(Particle.REDSTONE,loc,10,dustOptions);

    }

    public void removeFortress(String fortressName) {
        fortressContainer.remove(fortressName);
        fortressChunkKey.remove(fortressName);
    }

}
