package me.architetto.fwfortress.config;

import me.architetto.fwfortress.fortress.FortressService;
import org.bukkit.configuration.ConfigurationSection;

public class SettingsHandler {

    private static SettingsHandler instance;

    private int fortressHP;
    private int battleTimeLimit;
    private int startBattleDelay;
    private int distanceBetweenFortresses;

    private SettingsHandler() {
        if(instance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }

        //inizializzare

    }

    public static SettingsHandler getInstance() {
        if(instance == null) {
            instance= new SettingsHandler();
        }
        return instance;
    }

    public void loadSettings() {

        ConfigManager configManager = ConfigManager.getInstance();

        this.fortressHP = configManager.getInt(configManager.getConfig("Settings.yml"),"FORTRESS_HP");
        this.battleTimeLimit = configManager.getInt(configManager.getConfig("Settings.yml"),"BATTLE_TIME_LIMIT");
        this.startBattleDelay = configManager.getInt(configManager.getConfig("Settings.yml"),"START_BATTLE_DELAY");
        this.distanceBetweenFortresses = configManager.getInt(configManager.getConfig("Settings.yml"),"DISTANCE_BETWEEN_FORTRESSES");

    }

    public void loadFortress() {

        ConfigManager configManager = ConfigManager.getInstance();

        ConfigurationSection configurationSection = configManager.getConfig("Fortress.yml")
                .getConfigurationSection("");

        if (configurationSection == null)
            return;

        FortressService fortressService = FortressService.getInstance();

        for (String fortressName : configurationSection.getKeys(false)) {

            fortressService.newFortress(fortressName,
                    configManager.getStringRaw(configManager.getConfig("Fortress.yml"),fortressName + ".FIRTS_OWNER"),
                    configManager.getStringRaw(configManager.getConfig("Fortress.yml"),fortressName + ".OWNER"),
                    configManager.getLocation(configManager.getConfig("Fortress.yml"), fortressName + ".FORTRESS_POSITION"),
                    configManager.getInt(configManager.getConfig("Fortress.yml"), fortressName + ".FORTRESS_HP"));

        }

    }

    public void reload() {
        loadSettings();

        FortressService.getInstance().clearFortressContainer();
        loadFortress();
    }

    public int getFortressHP() { return this.fortressHP; }

    public void setFortressHP(int fortressHP) { this.fortressHP = fortressHP; }

    public int getBattleTimeLimit() { return this.battleTimeLimit; }

    public void setBattleTimeLimit(int battleTimeLimit) { this.battleTimeLimit = battleTimeLimit; }

    public int getStartBattleDelay() { return this.startBattleDelay; }

    public void setStartBattleDelay(int startBattleDelay) { this.startBattleDelay = startBattleDelay; }

    public int getDistanceBetweenFortresses() { return this.distanceBetweenFortresses; }

    public void setDistanceBetweenFortresses(int distanceBetweenFortresses) { this.distanceBetweenFortresses = distanceBetweenFortresses; }


}
