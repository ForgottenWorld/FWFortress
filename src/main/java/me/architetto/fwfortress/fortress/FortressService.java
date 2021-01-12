package me.architetto.fwfortress.fortress;

import me.architetto.fwfortress.FWFortress;
import me.architetto.fwfortress.config.ConfigManager;
import org.bukkit.*;

import java.util.*;

public class FortressService {

    private static FortressService fortressService;

    private HashMap<String,Fortress> fortressContainer;
    private HashMap<String,List<Long>> fortressChunkKey;

    private FortressService() {
        if(fortressService != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }

        this.fortressContainer = new HashMap<>();
        this.fortressChunkKey = new HashMap<>();

    }

    public static FortressService getInstance() {
        if(fortressService == null) {
            fortressService = new FortressService();
        }
        return fortressService;
    }

    public void saveFortress(Fortress fortress) {

        Bukkit.getScheduler().runTaskAsynchronously(FWFortress.plugin, () -> {

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

    public void removeFortress(String fortressName) {

        fortressContainer.remove(fortressName);
        fortressChunkKey.remove(fortressName);

        ConfigManager configManager = ConfigManager.getInstance();
        ConfigManager.getInstance().setData(configManager.getConfig("Fortress.yml"),fortressName,null);
    }

    public int getAmountOfFortressOwnedByTown(String townName) {
        return (int) this.fortressContainer.values().stream().filter(fortress -> fortress.getCurrentOwner().equals(townName)).count();
    }
}
