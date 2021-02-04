package me.architetto.fwfortress.fortress;

import me.architetto.fwfortress.FWFortress;
import me.architetto.fwfortress.config.ConfigManager;
import org.bukkit.*;

import java.util.*;

public class FortressService {

    private static FortressService fortressService;

    private List<Fortress> fortressContainer;

    private FortressService() {
        if(fortressService != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }

        this.fortressContainer = new ArrayList<>();

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
                    fortress.getCunkKeys());

        });
    }

    public Optional<Fortress> getFortress(String name) {
        return fortressContainer.stream()
                .filter(fortress -> fortress.getFortressName().contains(name)).findFirst();
    }

    public Optional<Fortress> getFortress(long chunkKey) {
        return fortressContainer.stream()
                .filter(fortress -> fortress.getCunkKeys().contains(chunkKey)).findFirst();
    }

    public List<Fortress> getFortressContainer() {
        return this.fortressContainer;
    }

    public void clearFortressContainer() {
        this.fortressContainer.clear();
    }

    public void removeFortress(Fortress fortress) {

        ConfigManager configManager = ConfigManager.getInstance();
        ConfigManager.getInstance().setData(configManager.getConfig("Fortress.yml"),fortress.getFortressName(),null);

        fortressContainer.remove(fortress);

    }

    public int getAmountOfFortressOwnedByTown(String townName) {
        return (int) this.fortressContainer.stream().filter(fortress -> fortress.getCurrentOwner().equals(townName)).count();
    }
}
