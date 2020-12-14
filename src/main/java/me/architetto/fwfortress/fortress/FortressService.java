package me.architetto.fwfortress.fortress;

import me.architetto.fwfortress.config.ConfigManager;
import me.architetto.fwfortress.config.SettingsHandler;
import me.architetto.fwfortress.util.ChatFormatter;
import org.bukkit.*;
import org.bukkit.entity.Player;

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

    public boolean newFortress(String fortressName, String firstOwner, String currentOwner, Location fortressPosition, int fortressHP) {

        if (fortressContainer.containsKey(fortressName)) {
            return false;
        }

        Fortress fortress = new Fortress(fortressName, firstOwner, currentOwner, fortressPosition, fortressHP);

        //todo: nel caso si dia la possibilità di cambiare il nome di una fortezza, ricorda di modificare anche il nome in
        //todo: questi array
        this.fortressContainer.put(fortressName, fortress);
        this.fortressChunkKey.put(fortressName,fortress.getChunkKeys());

        return true;
    }

    public void fortressCrationHandler(Player sender, Location location) {

        if(!playerCreationMode.contains(sender.getUniqueId())) {
            return;
        }

        if (newFortress(playerFortressNameCreation.get(sender.getUniqueId()),
                playerFortressOwnerCreation.get(sender.getUniqueId()),
                playerFortressOwnerCreation.get(sender.getUniqueId()),
                location,
                SettingsHandler.getInstance().getFortressHP())) {

            spawnParticleEffectAtBlock(location); //todo: effetto da migliorare

            sender.sendMessage(ChatFormatter.chatHeaderNewFort());
            sender.sendMessage(ChatFormatter.formatListMessage(ChatColor.AQUA + "NOME FORTEZZA : " +
                    ChatColor.YELLOW + playerFortressNameCreation.get(sender.getUniqueId())));
            sender.sendMessage(ChatFormatter.formatListMessage(ChatColor.AQUA + "OWNER : " +
                    ChatColor.YELLOW + playerFortressOwnerCreation.get(sender.getUniqueId())));
            sender.sendMessage(ChatFormatter.chatFooter());

            ConfigManager configManager = ConfigManager.getInstance();

            configManager.setData(configManager.getConfig("Fortress.yml"),
                    playerFortressNameCreation.get(sender.getUniqueId()) + ".FIRTS_OWNER", playerFortressOwnerCreation.get(sender.getUniqueId()));
            configManager.setData(configManager.getConfig("Fortress.yml"),
                    playerFortressNameCreation.get(sender.getUniqueId()) + ".OWNER", playerFortressOwnerCreation.get(sender.getUniqueId()));
            configManager.addLocation(configManager.getConfig("Fortress.yml"),
                    location,playerFortressNameCreation.get(sender.getUniqueId()) + ".FORTRESS_POSITION");
            configManager.setData(configManager.getConfig("Fortress.yml"),
                    playerFortressNameCreation.get(sender.getUniqueId()) + ".FORTRESS_HP", SettingsHandler.getInstance().getFortressHP());

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

    public Optional<Fortress> getFortress(long chunkKey) {
        //todo questo si può fare sicuramente molto meglio ma sono le 5 di mattina quindi nisba
        String fortressName = null;
        for (String s : fortressChunkKey.keySet()) {
            if (fortressChunkKey.get(s).contains(chunkKey))
                fortressName = s;

        }

        if (fortressName == null)
            return Optional.empty();

        return fortressContainer.containsKey(fortressName) ? Optional.of(fortressContainer.get(fortressName)) : Optional.empty();

    }

    public HashMap<String, Fortress> getFortressContainer() {
        return this.fortressContainer;
    }

    public void clearFortressContainer() {
        this.fortressContainer.clear();
        this.fortressChunkKey.clear();
    }

    public void addProtectedChunkKeys(Fortress fortress) {
        this.fortressChunkKey.put(fortress.getFortressName(), fortress.getChunkKeys());
    }

    public HashMap<String,List<Long>> getProtectedChunkKeys() {
        return this.fortressChunkKey;
    }

    public void spawnParticleEffectAtBlock(Location loc) {

        loc.add(0.5,1,0.5);
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(255, 69, 0), 5);
        loc.getWorld().spawnParticle(Particle.REDSTONE,loc,10,dustOptions);

    }

    public void removeFortress(String fortressName) {
        fortressContainer.remove(fortressName);
        fortressChunkKey.remove(fortressName);
    }

}
