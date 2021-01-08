package me.architetto.fwfortress.fortress;

import me.architetto.fwfortress.FWFortress;
import me.architetto.fwfortress.api.FortService;
import me.architetto.fwfortress.config.ConfigManager;
import me.architetto.fwfortress.config.SettingsHandler;
import me.architetto.fwfortress.util.ChatFormatter;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.*;

public class FortressService implements FortService {

    private static FortressService fortressService;

    private HashMap<String,Fortress> fortressContainer;
    private HashMap<String,List<Long>> fortressChunkKey;

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

    public boolean newFortress(String fortressName, String firstOwner,
                               String currentOwner, Location fortressPosition,
                               int fortressHP, long lastBattle, long lastRepair) {

        if (fortressContainer.containsKey(fortressName)) {
            return false;
        }

        Fortress fortress = new Fortress(fortressName, firstOwner, currentOwner,
                fortressPosition, fortressHP, lastBattle, lastRepair);

        this.fortressContainer.put(fortressName, fortress);
        this.fortressChunkKey.put(fortressName, fortress.getChunkKeys());

        return true;
    }

    public void loadFortress(String fortressName, String firstOwner,
                             String currentOwner, Location fortressPosition,
                             int fortressHP, long lastBattle, long lastRepair, List<Long> fortressChunkKeys) {

        if (fortressContainer.containsKey(fortressName)) {
            Bukkit.getConsoleSender().sendMessage(ChatFormatter.formatErrorMessage("individuate fortezza omonime ..."));
        }

        Fortress fortress = new Fortress(fortressName, firstOwner, currentOwner,
                fortressPosition, fortressHP, lastBattle, lastRepair);

        this.fortressContainer.put(fortressName, fortress);
        this.fortressChunkKey.put(fortressName, fortressChunkKeys);

    }

    public void saveFortress(Fortress fortress) {

        Bukkit.getScheduler().runTaskAsynchronously(FWFortress.plugin, () -> {

            Bukkit.getConsoleSender().sendMessage();

            ConfigManager configManager = ConfigManager.getInstance();

            configManager.setData(configManager.getConfig("Fortress.yml"),
                    fortress.getFortressName() + ".FIRTS_OWNER", fortress.getFirstOwner());
            configManager.setData(configManager.getConfig("Fortress.yml"),
                    fortress.getFortressName() + ".OWNER", fortress.getCurrentOwner());
            configManager.addLocation(configManager.getConfig("Fortress.yml"),
                    fortress.getLocation(),fortress.getFortressName() + ".FORTRESS_POSITION");
            configManager.setData(configManager.getConfig("Fortress.yml"),
                    fortress.getFortressName() + ".FORTRESS_HP", fortress.getCurrentHP());
            configManager.setData(configManager.getConfig("Fortress.yml"),
                    fortress.getFortressName() + ".LAST_BATTLE", fortress.getLastBattle());
            configManager.setData(configManager.getConfig("Fortress.yml"),
                    fortress.getFortressName() + ".LAST_REPAIR", fortress.getLastRepair());
            configManager.setData(configManager.getConfig("Fortress.yml"),
                    fortress.getFortressName() + ".CHUNKKEYS",
                    fortressChunkKey.get(fortress.getFortressName()));

        });
    }

    public void fortressCrationHandler(Player sender, Location location) {

        if(!playerCreationMode.contains(sender.getUniqueId())) {
            return;
        }

        if (fortressContainer.values().stream()
                .filter(fortress -> fortress.getWorldName().equals(location.getWorld().getName()))
                .anyMatch(fortress -> fortress.getFortressVector()
                        .distance(location.toVector()) < SettingsHandler.getInstance().getDistanceBetweenFortresses())) {

            sender.sendMessage(ChatFormatter.formatErrorMessage("Questo chunk è troppo vicino ad un'altra fortezza"));

            this.playerFortressNameCreation.remove(sender.getUniqueId());
            this.playerFortressOwnerCreation.remove(sender.getUniqueId());
            this.playerCreationMode.remove(sender.getUniqueId());

            return;
        }

        if (newFortress(playerFortressNameCreation.get(sender.getUniqueId()),
                playerFortressOwnerCreation.get(sender.getUniqueId()),
                playerFortressOwnerCreation.get(sender.getUniqueId()),
                location,
                SettingsHandler.getInstance().getFortressHP(),
                0,0)) {

            spawnParticleEffectAtBlock(location.clone());

            sender.sendMessage(ChatFormatter.chatHeaderFortInfo());
            sender.sendMessage(ChatFormatter.formatListMessage(ChatColor.AQUA + "NOME FORTEZZA : " +
                    ChatColor.YELLOW + playerFortressNameCreation.get(sender.getUniqueId())));
            sender.sendMessage(ChatFormatter.formatListMessage(ChatColor.AQUA + "OWNER : " +
                    ChatColor.YELLOW + playerFortressOwnerCreation.get(sender.getUniqueId())));
            sender.sendMessage(ChatFormatter.chatFooter());

            saveFortress(this.fortressContainer.get(playerFortressNameCreation.get(sender.getUniqueId())));

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

    public HashMap<String,List<Long>> getProtectedChunkKeys() {
        return this.fortressChunkKey;
    }

    public void spawnParticleEffectAtBlock(Location loc) {

        loc.add(0.5,2,0.5);
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(255, 69, 0), 5);
        loc.getWorld().spawnParticle(Particle.REDSTONE,loc,10,dustOptions);

    }

    public void removeFortress(String fortressName) {
        fortressContainer.remove(fortressName);
        fortressChunkKey.remove(fortressName);
        ConfigManager configManager = ConfigManager.getInstance();
        configManager.setData(configManager.getConfig("Fortress.yml"),fortressName,null);
    }

    @Override
    public int getFortressAmount(String cityName) {
        return (int) this.fortressContainer.values().stream().filter(fortress -> fortress.getCurrentOwner().equals(cityName)).count();
    }
}
