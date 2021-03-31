package me.architetto.fwfortress.fortress;

import me.architetto.fwfortress.FWFortress;
import me.architetto.fwfortress.config.ConfigManager;
import org.bukkit.*;

import java.util.*;

public class FortressService {

    private static FortressService fortressService;

    private Set<Fortress> fortressContainer;

    private FortressService() {
        if(fortressService != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }

        this.fortressContainer = new HashSet<>();

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
                     new ArrayList<>(fortress.getCunkKeys()));

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
        return fortressContainer.stream()
                .filter(fortress -> fortress.getName().contains(name)).findFirst();
    }

    public Optional<Fortress> getFortress(long chunkKey, UUID worldUUID) {
        return fortressContainer.stream()
                .filter(fortress -> fortress.getCunkKeys().contains(chunkKey)
                        && fortress.getLightLocation().getWorldUUID().equals(worldUUID)).findFirst();
    }

    public Set<Fortress> getFortressContainer() {
        return fortressContainer;
    }

    public void clearFortressContainer() {
        this.fortressContainer.clear();
    }

    public void removeFortress(Fortress fortress) {

        ConfigManager configManager = ConfigManager.getInstance();

        ConfigManager.getInstance().setData(configManager.getConfig("Fortress.yml"),fortress.getName(),null);

        fortressContainer.remove(fortress);

    }

    public int getAmountOfFortressOwnedByTown(String townName) {
        return (int) this.fortressContainer
                .stream()
                .filter(fortress -> fortress.getOwner() != null
                        && fortress.getOwner().equals(townName))
                .count();
    }
}
